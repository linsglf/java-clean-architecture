package com.cinetech.api.dominio.modelos.pagamento;

import com.cinetech.api.dominio.enums.MetodoPagamento;
import com.cinetech.api.dominio.enums.StatusPagamento;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Pagamento {
    private final PagamentoId id;
    private final BigDecimal valor;
    private final MetodoPagamento metodoPagamento;
    private StatusPagamento status;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String idTransacaoGateway; // ID da transação no provedor de pagamento externo

    // O pagamento está relacionado a UM Ingresso OU a UMA Reserva de Evento, mas não ambos.
    private final IngressoId ingressoId;         // Pode ser nulo
    private final ReservaEventoId reservaEventoId; // Pode ser nulo

    // Construtor para pagamento de ingresso
    public Pagamento(IngressoId ingressoId, BigDecimal valor, MetodoPagamento metodoPagamento) {
        this(PagamentoId.novo(), valor, metodoPagamento, StatusPagamento.PENDENTE,
                LocalDateTime.now(), LocalDateTime.now(), null,
                Objects.requireNonNull(ingressoId, "ID do Ingresso não pode ser nulo para pagamento de ingresso."),
                null);
    }

    // Construtor para pagamento de reserva de evento
    public Pagamento(ReservaEventoId reservaEventoId, BigDecimal valor, MetodoPagamento metodoPagamento) {
        this(PagamentoId.novo(), valor, metodoPagamento, StatusPagamento.PENDENTE,
                LocalDateTime.now(), LocalDateTime.now(), null,
                null,
                Objects.requireNonNull(reservaEventoId, "ID da Reserva de Evento não pode ser nulo para pagamento de reserva."));
    }

    // Construtor completo para reconstituição
    public Pagamento(PagamentoId id, BigDecimal valor, MetodoPagamento metodoPagamento, StatusPagamento status,
                     LocalDateTime dataCriacao, LocalDateTime dataAtualizacao, String idTransacaoGateway,
                     IngressoId ingressoId, ReservaEventoId reservaEventoId) {
        this.id = Objects.requireNonNull(id, "ID do Pagamento não pode ser nulo.");

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            // Permitir pagamento de valor zero para promoções 100% ou pontos?
            // O documento diz "crédito proporcional ao valor pago" (F4) e "acumula pontos baseados no valor do ingresso" (F6)
            // "Créditos podem ser trocados por ingressos" (Source 24) -> isso pode gerar um pagamento de valor zero.
            // Vamos permitir valor zero, mas não negativo.
            if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Valor do pagamento não pode ser nulo ou negativo. Recebido: " + valor);
            }
        }
        this.valor = valor;
        this.metodoPagamento = Objects.requireNonNull(metodoPagamento, "Método de pagamento não pode ser nulo.");
        this.status = Objects.requireNonNull(status, "Status do pagamento não pode ser nulo.");
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "Data de criação não pode ser nula.");
        this.dataAtualizacao = Objects.requireNonNull(dataAtualizacao, "Data de atualização não pode ser nula.");
        this.idTransacaoGateway = idTransacaoGateway;

        if (ingressoId == null && reservaEventoId == null) {
            throw new IllegalArgumentException("Pagamento deve estar associado a um Ingresso ou a uma Reserva de Evento.");
        }
        if (ingressoId != null && reservaEventoId != null) {
            throw new IllegalArgumentException("Pagamento não pode estar associado a um Ingresso E a uma Reserva de Evento simultaneamente.");
        }
        this.ingressoId = ingressoId;
        this.reservaEventoId = reservaEventoId;
    }

    // Getters
    public PagamentoId getId() { return id; }
    public BigDecimal getValor() { return valor; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public StatusPagamento getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public String getIdTransacaoGateway() { return idTransacaoGateway; }
    public IngressoId getIngressoId() { return ingressoId; }
    public ReservaEventoId getReservaEventoId() { return reservaEventoId; }


    // Métodos de Negócio para alterar o estado
    public void aprovar(String idTransacaoGateway) {
        if (this.status != StatusPagamento.PENDENTE && this.status != StatusPagamento.PROCESSANDO) {
            throw new IllegalStateException("Pagamento com ID " + this.id + " não pode ser aprovado pois seu status é " + this.status);
        }
        this.status = StatusPagamento.APROVADO;
        this.idTransacaoGateway = idTransacaoGateway;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void rejeitar() {
        if (this.status != StatusPagamento.PENDENTE && this.status != StatusPagamento.PROCESSANDO) {
            throw new IllegalStateException("Pagamento com ID " + this.id + " não pode ser rejeitado pois seu status é " + this.status);
        }
        this.status = StatusPagamento.REJEITADO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void iniciarProcessamento() {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Pagamento com ID " + this.id + " só pode iniciar processamento se estiver pendente. Status atual: " + this.status);
        }
        this.status = StatusPagamento.PROCESSANDO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void cancelar() { // Cancelamento pelo sistema/usuário antes de aprovação
        if (this.status == StatusPagamento.APROVADO || this.status == StatusPagamento.REEMBOLSADO) {
            throw new IllegalStateException("Pagamento com ID " + this.id + " que já foi aprovado ou reembolsado não pode ser cancelado desta forma.");
        }
        this.status = StatusPagamento.CANCELADO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void registrarErroProcessamento() {
        // Pode ser chamado se houver falha de comunicação com gateway, etc.
        this.status = StatusPagamento.ERRO_PROCESSAMENTO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Para F4 (Reembolso) ou F7 (Cancelamento de reserva paga)
    public void reembolsar(String idTransacaoReembolsoGateway) {
        if(this.status != StatusPagamento.APROVADO) {
            throw new IllegalStateException("Apenas pagamentos aprovados podem ser reembolsados. Status atual: " + this.status);
        }
        this.status = StatusPagamento.REEMBOLSADO;
        this.idTransacaoGateway = idTransacaoReembolsoGateway; // Pode sobrescrever ou adicionar um novo campo para ID de reembolso
        this.dataAtualizacao = LocalDateTime.now();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return id.equals(pagamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String associacao = ingressoId != null ? "ingressoId=" + ingressoId : "reservaEventoId=" + reservaEventoId;
        return "Pagamento{" +
                "id=" + id +
                ", valor=" + valor +
                ", status=" + status +
                ", metodo=" + metodoPagamento +
                ", " + associacao +
                '}';
    }
}
