package com.cinetech.api.dominio.reservaevento;

import com.cinetech.api.dominio.cliente.ClienteId;
import com.cinetech.api.dominio.enums.StatusReservaEvento;
import com.cinetech.api.dominio.pagamento.PagamentoId;
import com.cinetech.api.dominio.sala.SalaId;

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
    private BigDecimal valorCobrado; // Valor total da reserva da sala
    private final LocalDateTime dataSolicitacao;
    private PagamentoId pagamentoId; // Referência ao pagamento efetuado para esta reserva

    public static final int ANTECEDENCIA_MINIMA_HORAS = 48; // F7

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
        this.clienteId = Objects.requireNonNull(clienteId, "ID do Cliente não pode ser nulo.");
        this.salaId = Objects.requireNonNull(salaId, "ID da Sala não pode ser nulo.");
        setNomeEvento(nomeEvento);

        Objects.requireNonNull(dataHoraInicio, "Data e hora de início do evento não podem ser nulos.");
        Objects.requireNonNull(dataHoraFim, "Data e hora de fim do evento não podem ser nulos.");
        if (dataHoraFim.isBefore(dataHoraInicio) || dataHoraFim.equals(dataHoraInicio)) {
            throw new IllegalArgumentException("Data e hora de fim do evento deve ser posterior à data e hora de início.");
        }
        // Validação de antecedência mínima (F7)
        if (dataHoraInicio.isBefore(LocalDateTime.now().plusHours(ANTECEDENCIA_MINIMA_HORAS))) {
            throw new IllegalArgumentException("Reservas de sala para eventos devem ser feitas com pelo menos " + ANTECEDENCIA_MINIMA_HORAS + " horas de antecedência.");
        }
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;

        this.status = Objects.requireNonNull(status, "Status da reserva de evento não pode ser nulo.");

        if (valorCobrado == null || valorCobrado.compareTo(BigDecimal.ZERO) < 0) { // Pode ser 0 para eventos gratuitos?
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

    // Métodos de Negócio
    /**
     * Confirma a reserva após o pagamento ser efetuado. (F7)
     * Associa o ID do pagamento à reserva.
     */
    public void confirmarPagamentoEReserva(PagamentoId pagamentoConfirmadoId) {
        Objects.requireNonNull(pagamentoConfirmadoId, "ID do pagamento confirmado não pode ser nulo.");
        if (this.status != StatusReservaEvento.SOLICITADA && this.status != StatusReservaEvento.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Reserva com ID " + this.id + " não pode ser confirmada pois seu status é " + this.status);
        }
        this.pagamentoId = pagamentoConfirmadoId;
        this.status = StatusReservaEvento.CONFIRMADA;
        // Idealmente, emitir evento ReservaEventoConfirmadaEvent
    }

    /**
     * Marca a reserva como aguardando pagamento.
     */
    public void aguardarPagamento() {
        if (this.status != StatusReservaEvento.SOLICITADA) {
            throw new IllegalStateException("Reserva com ID " + this.id + " não está no status SOLICITADA para aguardar pagamento. Status atual: " + this.status);
        }
        this.status = StatusReservaEvento.AGUARDANDO_PAGAMENTO;
    }

    /**
     * Cancela a reserva (pelo cliente ou sistema).
     */
    public void cancelar(boolean peloCliente) {
        if (this.status == StatusReservaEvento.CONFIRMADA) {
            // Regras para cancelamento de reserva confirmada (ex: política de reembolso)
            // podem ser complexas e envolver Domain Services ou Application Services.
            // Por ora, permite o cancelamento, mas o reembolso seria outra operação.
            System.out.println("WARN DOMINIO: Cancelando reserva CONFIRMADA. Lógica de reembolso/taxas não implementada na entidade.");
        }
        if (this.status == StatusReservaEvento.CANCELADA_PELO_CLIENTE || this.status == StatusReservaEvento.CANCELADA_PELO_SISTEMA) {
            throw new IllegalStateException("Reserva com ID " + this.id + " já está cancelada.");
        }
        this.status = peloCliente ? StatusReservaEvento.CANCELADA_PELO_CLIENTE : StatusReservaEvento.CANCELADA_PELO_SISTEMA;
        // Idealmente, emitir evento ReservaEventoCanceladaEvent
    }

    public boolean conflitaCom(LocalDateTime outroInicio, LocalDateTime outroFim) {
        Objects.requireNonNull(outroInicio, "Início do outro período não pode ser nulo.");
        Objects.requireNonNull(outroFim, "Fim do outro período não pode ser nulo.");
        // Verifica se há sobreposição de intervalos: (StartA <= EndB) and (EndA >= StartB)
        return (this.dataHoraInicio.isBefore(outroFim) || this.dataHoraInicio.isEqual(outroFim)) &&
                (this.dataHoraFim.isAfter(outroInicio) || this.dataHoraFim.isEqual(outroInicio));
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
