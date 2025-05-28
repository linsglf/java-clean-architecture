package com.cinetech.api.dominio.modelos.reservaevento;

import com.cinetech.api.dominio.enums.StatusReservaEvento;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.pagamento.PagamentoId;
import com.cinetech.api.dominio.modelos.sala.SalaId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReservaEvento {
    private final ReservaEventoId id;
    private final ClienteId clienteId;
    private final SalaId salaId;
    private String nomeEvento;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusReservaEvento status;
    private BigDecimal valorCobrado;
    private final LocalDateTime dataSolicitacao;
    private PagamentoId pagamentoId; // Referência ao pagamento efetuado

    public static final int ANTECEDENCIA_MINIMA_EM_HORAS = 48; //

    // Construtor para nova reserva de evento
    public ReservaEvento(ClienteId clienteId, SalaId salaId, String nomeEvento,
                         LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, BigDecimal valorCobrado) {
        this(ReservaEventoId.novo(), clienteId, salaId, nomeEvento, dataHoraInicio, dataHoraFim,
                StatusReservaEvento.SOLICITADA, valorCobrado, LocalDateTime.now(), null);
    }

    // Construtor completo para reconstituição
    public ReservaEvento(ReservaEventoId id, ClienteId clienteId, SalaId salaId, String nomeEvento,
                         LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, StatusReservaEvento status,
                         BigDecimal valorCobrado, LocalDateTime dataSolicitacao, PagamentoId pagamentoId) {
        this.id = Objects.requireNonNull(id, "ID da Reserva de Evento não pode ser nulo.");
        this.clienteId = Objects.requireNonNull(clienteId, "ID do Cliente não pode ser nulo para reserva de evento.");
        this.salaId = Objects.requireNonNull(salaId, "ID da Sala não pode ser nulo para reserva de evento.");
        setNomeEvento(nomeEvento);

        Objects.requireNonNull(dataHoraInicio, "Data e hora de início do evento não podem ser nulos.");
        Objects.requireNonNull(dataHoraFim, "Data e hora de fim do evento não podem ser nulos.");
        if (dataHoraFim.isBefore(dataHoraInicio) || dataHoraFim.equals(dataHoraInicio)) {
            throw new IllegalArgumentException("Data e hora de fim do evento deve ser posterior à data e hora de início.");
        }
        // Validação de antecedência mínima (F7, Regra de Negócio)
        if (dataHoraInicio.isBefore(LocalDateTime.now().plusHours(ANTECEDENCIA_MINIMA_EM_HORAS))) {
            throw new IllegalArgumentException("Reservas de sala para eventos devem ser feitas com pelo menos " +
                    ANTECEDENCIA_MINIMA_EM_HORAS + " horas de antecedência. Data solicitada: " + dataHoraInicio);
        }
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;

        this.status = Objects.requireNonNull(status, "Status da reserva de evento não pode ser nulo.");

        if (valorCobrado == null || valorCobrado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor cobrado pela reserva não pode ser nulo ou negativo. Recebido: " + valorCobrado);
        }
        this.valorCobrado = valorCobrado;
        this.dataSolicitacao = Objects.requireNonNull(dataSolicitacao, "Data de solicitação não pode ser nula.");
        this.pagamentoId = pagamentoId; // Pode ser nulo inicialmente
    }

    // Getters
    public ReservaEventoId getId() { return id; }
    public ClienteId getClienteId() { return clienteId; }
    public SalaId getSalaId() { return salaId; }
    public String getNomeEvento() { return nomeEvento; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public StatusReservaEvento getStatus() { return status; }
    public BigDecimal getValorCobrado() { return valorCobrado; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public PagamentoId getPagamentoId() { return pagamentoId; }

    // Setters (controlados)
    public void setNomeEvento(String nomeEvento) {
        if (nomeEvento == null || nomeEvento.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do evento não pode ser vazio.");
        }
        this.nomeEvento = nomeEvento.trim();
    }

    public void setValorCobrado(BigDecimal valorCobrado) {
        if (valorCobrado == null || valorCobrado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor cobrado pela reserva não pode ser nulo ou negativo. Recebido: " + valorCobrado);
        }
        this.valorCobrado = valorCobrado;
    }


    // Métodos de Negócio

    /**
     * Confirma a reserva após o pagamento ser efetuado.
     * Associa o ID do pagamento à reserva.
     * A sala reservada fica bloqueada no sistema para uso comum. (Esta parte do bloqueio da sala
     * seria coordenada por um Application Service, que atualizaria o status da Sala ou criaria
     * um bloqueio específico para ela).
     */
    public void registrarPagamentoConfirmado(PagamentoId pagamentoConfirmadoId) {
        Objects.requireNonNull(pagamentoConfirmadoId, "ID do pagamento confirmado não pode ser nulo.");
        if (this.status != StatusReservaEvento.SOLICITADA && this.status != StatusReservaEvento.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Reserva com ID " + this.id + " não pode ter pagamento confirmado pois seu status é " + this.status);
        }
        this.pagamentoId = pagamentoConfirmadoId;
        this.status = StatusReservaEvento.CONFIRMADA;
    }

    /**
     * Marca a reserva como aguardando pagamento.
     * O pagamento deve ser antecipado.
     */
    public void marcarComoAguardandoPagamento() {
        if (this.status != StatusReservaEvento.SOLICITADA) {
            throw new IllegalStateException("Reserva com ID " + this.id + " não está no status SOLICITADA para aguardar pagamento. Status atual: " + this.status);
        }
        this.status = StatusReservaEvento.AGUARDANDO_PAGAMENTO;
    }

    /**
     * Cancela a reserva (pelo cliente ou sistema).
     */
    public void cancelar(boolean canceladoPeloCliente) {
        // Regras de negócio para cancelamento podem ser adicionadas aqui,
        // como verificar se ainda é possível cancelar, se há taxas, etc.
        // Por exemplo, se já foi CONFIRMADA, pode haver políticas de reembolso.
        if (this.status == StatusReservaEvento.REALIZADA || this.status == StatusReservaEvento.CANCELADA_PELO_CLIENTE || this.status == StatusReservaEvento.CANCELADA_PELO_SISTEMA) {
            throw new IllegalStateException("Reserva com ID " + this.id + " já está finalizada ou cancelada (status: "+this.status+").");
        }

        this.status = canceladoPeloCliente ? StatusReservaEvento.CANCELADA_PELO_CLIENTE : StatusReservaEvento.CANCELADA_PELO_SISTEMA;
        // Se havia um pagamento associado e a reserva é cancelada, a lógica de reembolso/estorno
        // seria tratada por um Application Service.
    }

    /**
     * Verifica se esta reserva de evento conflita com outro período de tempo.
     * Usado por Application Services para garantir que sessões não podem ser sobrepostas.
     */
    public boolean conflitaComPeriodo(LocalDateTime outroInicio, LocalDateTime outroFim) {
        Objects.requireNonNull(outroInicio, "Início do outro período não pode ser nulo.");
        Objects.requireNonNull(outroFim, "Fim do outro período não pode ser nulo.");
        if (outroFim.isBefore(outroInicio) || outroFim.equals(outroInicio)) {
            throw new IllegalArgumentException("Fim do outro período deve ser posterior ao início.");
        }
        // Verifica sobreposição: (StartA < EndB) and (EndA > StartB)
        return this.dataHoraInicio.isBefore(outroFim) && this.dataHoraFim.isAfter(outroInicio);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservaEvento that = (ReservaEvento) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ReservaEvento{" +
                "id=" + id +
                ", nomeEvento='" + nomeEvento + '\'' +
                ", clienteId=" + clienteId +
                ", salaId=" + salaId +
                ", dataHoraInicio=" + dataHoraInicio +
                ", status=" + status +
                '}';
    }
}
