package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.MetodoPagamento;
import com.cinetech.api.dominio.enums.StatusPagamento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pagamento")
public class PagamentoJpa {

    @Id
    private UUID id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MetodoPagamento metodoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusPagamento status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @Column(length = 100)
    private String idTransacaoGateway;

    @Column(name = "ingresso_id") // Armazena o UUID do IngressoId
    private UUID ingressoId;

    @Column(name = "reserva_evento_id") // Armazena o UUID do ReservaEventoId
    private UUID reservaEventoId;

    public PagamentoJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    public String getIdTransacaoGateway() { return idTransacaoGateway; }
    public void setIdTransacaoGateway(String idTransacaoGateway) { this.idTransacaoGateway = idTransacaoGateway; }
    public UUID getIngressoId() { return ingressoId; }
    public void setIngressoId(UUID ingressoId) { this.ingressoId = ingressoId; }
    public UUID getReservaEventoId() { return reservaEventoId; }
    public void setReservaEventoId(UUID reservaEventoId) { this.reservaEventoId = reservaEventoId; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagamentoJpa that = (PagamentoJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
