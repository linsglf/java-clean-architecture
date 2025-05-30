package com.cinetech.api.aplicacao;

import com.cinetech.api.dominio.enums.StatusPagamento;
import com.cinetech.api.dominio.enums.StatusReservaEvento;
import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.pagamento.Pagamento;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEvento;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.modelos.sala.Sala;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.repositorios.ClienteRepositorio;
import com.cinetech.api.dominio.repositorios.PagamentoRepositorio;
import com.cinetech.api.dominio.repositorios.ReservaEventoRepositorio;
import com.cinetech.api.dominio.repositorios.SalaRepositorio;
import com.cinetech.api.dominio.servicos.AgendamentoServico.AgendamentoServico;
import com.cinetech.api.infraestrutura.web.dto.reservaeventos.ConfirmarPagamentoReservaRequestDTO;
import com.cinetech.api.infraestrutura.web.dto.reservaeventos.CriarReservaEventoRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ReservaEventoAplicacao {

    private final ReservaEventoRepositorio reservaEventoRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final SalaRepositorio salaRepositorio;
    private final PagamentoRepositorio pagamentoRepositorio; // Para F7 "Pagamento antecipado"
    private final AgendamentoServico agendamentoService;

    public ReservaEventoAplicacao(ReservaEventoRepositorio reservaEventoRepositorio,
                                  ClienteRepositorio clienteRepositorio,
                                  SalaRepositorio salaRepositorio,
                                  PagamentoRepositorio pagamentoRepositorio,
                                  AgendamentoServico agendamentoService) {
        this.reservaEventoRepositorio = reservaEventoRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.salaRepositorio = salaRepositorio;
        this.pagamentoRepositorio = pagamentoRepositorio;
        this.agendamentoService = agendamentoService;
    }

    /**
     * Caso de Uso: Cliente solicita uma reserva de sala para evento. (F7)
     * "Reservas devem ser feitas com pelo menos 48 horas de antecedência"
     * "A sala reservada deve ficar invisível na grade pública enquanto estiver bloqueada para evento." (Source 27)
     * - O bloqueio efetivo da sala para sessões comuns seria verificado pelo AgendamentoService.
     */
    @Transactional
    public ReservaEvento solicitarReservaDeSala(CriarReservaEventoRequestDTO request) {
        Objects.requireNonNull(request, "Dados da requisição de reserva não podem ser nulos.");
        ClienteId clienteId = ClienteId.de(request.getClienteId());
        SalaId salaId = SalaId.de(request.getSalaId());

        Cliente cliente = clienteRepositorio.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
        Sala sala = salaRepositorio.buscarPorId(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada: " + salaId));

        if (!sala.isDisponivelParaEventos()) {
            throw new IllegalStateException("Sala " + sala.getNome() + " não está disponível para reserva de eventos.");
        }

        // Validação de antecedência (já no construtor de ReservaEvento)
        // Validação de conflito de horário
        if (agendamentoService.verificarConflitoAgendamento(salaId, request.getDataHoraInicio(), request.getDataHoraFim(), Optional.empty(), Optional.empty())) {
            throw new IllegalStateException("Conflito de horário para a sala " + sala.getNome() + " no período solicitado.");
        }

        ReservaEvento novaReserva = new ReservaEvento(
                cliente.getId(), // Passando o ClienteId da entidade de domínio
                sala.getId(),    // Passando o SalaId da entidade de domínio
                request.getNomeEvento(),
                request.getDataHoraInicio(),
                request.getDataHoraFim(),
                request.getValorEstimado() // O valor pode ser estimado inicialmente e confirmado depois
        );

        // Por padrão, o construtor de ReservaEvento define o status como SOLICITADA.
        // Poderíamos mudar para AGUARDANDO_PAGAMENTO aqui.
        novaReserva.marcarComoAguardandoPagamento(); // Método da entidade ReservaEvento

        return reservaEventoRepositorio.salvar(novaReserva);
    }

    /**
     * Caso de Uso: Confirmar pagamento de uma reserva de evento. (F7)
     * "O pagamento deve ser antecipado e confirmado para a reserva ser efetivada."
     */
    @Transactional
    public ReservaEvento confirmarPagamentoReserva(ConfirmarPagamentoReservaRequestDTO request) {
        Objects.requireNonNull(request, "Dados da confirmação de pagamento da reserva não podem ser nulos.");
        ReservaEventoId reservaId = ReservaEventoId.de(request.getReservaEventoId());

        ReservaEvento reserva = reservaEventoRepositorio.buscarPorId(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva de evento com ID " + reservaId + " não encontrada."));

        if (reserva.getStatus() != StatusReservaEvento.AGUARDANDO_PAGAMENTO && reserva.getStatus() != StatusReservaEvento.SOLICITADA) {
            throw new IllegalStateException("Pagamento para a reserva " + reservaId + " não pode ser confirmado. Status atual: " + reserva.getStatus());
        }

        // Lógica de pagamento SIMPLIFICADA (reincorporada aqui)
        Pagamento pagamento = new Pagamento(reserva.getId(), reserva.getValorCobrado(), request.getMetodoPagamento());
        String idTransacaoSimulada = "PAY_REVNT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        pagamento.aprovar(idTransacaoSimulada); // Aprova diretamente
        Pagamento pagamentoSalvo = pagamentoRepositorio.salvar(pagamento);

        if (pagamentoSalvo.getStatus() == StatusPagamento.APROVADO) {
            reserva.registrarPagamentoConfirmado(pagamentoSalvo.getId()); // Método na entidade ReservaEvento
            return reservaEventoRepositorio.salvar(reserva);
        } else {
            // Pagamento falhou, a reserva pode ser cancelada pelo sistema
            reserva.cancelar(false); // false = cancelado pelo sistema
            reservaEventoRepositorio.salvar(reserva);
            throw new IllegalStateException("Pagamento para reserva " + reservaId + " falhou. Reserva cancelada.");
        }
    }

    @Transactional(readOnly = true)
    public Optional<ReservaEvento> buscarReservaEventoPorId(ReservaEventoId reservaId) {
        Objects.requireNonNull(reservaId, "ID da Reserva de Evento não pode ser nulo.");
        return reservaEventoRepositorio.buscarPorId(reservaId);
    }

    /**
     * Caso de Uso: Listar salas disponíveis para eventos em um determinado período. (Source 50)
     * Este método é mais complexo pois envolve verificar a disponibilidade de *cada* sala.
     */
    @Transactional(readOnly = true)
    public List<Sala> listarSalasDisponiveisParaEvento(LocalDateTime inicioProposto, LocalDateTime fimProposto) {
        Objects.requireNonNull(inicioProposto, "Início proposto não pode ser nulo.");
        Objects.requireNonNull(fimProposto, "Fim proposto não pode ser nulo.");
        if (fimProposto.isBefore(inicioProposto) || fimProposto.equals(inicioProposto)) {
            throw new IllegalArgumentException("Fim proposto deve ser após o início proposto.");
        }
        if (inicioProposto.isBefore(LocalDateTime.now().plusHours(ReservaEvento.ANTECEDENCIA_MINIMA_EM_HORAS))) {
            throw new IllegalArgumentException("Busca por salas para eventos deve respeitar a antecedência mínima de " +
                    ReservaEvento.ANTECEDENCIA_MINIMA_EM_HORAS + " horas.");
        }


        List<Sala> todasSalasParaEventos = salaRepositorio.buscarSalasDisponiveisParaEventos();

        return todasSalasParaEventos.stream()
                .filter(sala -> !agendamentoService.verificarConflitoAgendamento(
                        sala.getId(), inicioProposto, fimProposto, Optional.empty(), Optional.empty()
                ))
                .collect(Collectors.toList());
    }
}
