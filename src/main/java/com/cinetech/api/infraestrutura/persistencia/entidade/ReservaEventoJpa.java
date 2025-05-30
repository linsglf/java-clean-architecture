package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.StatusReservaEvento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "reserva_evento")
public class ReservaEventoJpa {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpa cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaJpa sala;

    @Column(nullable = false, length = 200)
    private String nomeEvento;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(nullable = false)
    private LocalDateTime dataHoraFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusReservaEvento status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCobrado;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "pagamento_id") // Armazena o UUID do PagamentoId
    private UUID pagamentoId;

    public ReservaEventoJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ClienteJpa getCliente() { return cliente; }
    public void setCliente(ClienteJpa cliente) { this.cliente = cliente; }
    public SalaJpa getSala() { return sala; }
    public void setSala(SalaJpa sala) { this.sala = sala; }
    public String getNomeEvento() { return nomeEvento; }
    public void setNomeEvento(String nomeEvento) { this.nomeEvento = nomeEvento; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public StatusReservaEvento getStatus() { return status; }
    public void setStatus(StatusReservaEvento status) { this.status = status; }
    public BigDecimal getValorCobrado() { return valorCobrado; }
    public void setValorCobrado(BigDecimal valorCobrado) { this.valorCobrado = valorCobrado; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    public UUID getPagamentoId() { return pagamentoId; }
    public void setPagamentoId(UUID pagamentoId) { this.pagamentoId = pagamentoId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservaEventoJpa that = (ReservaEventoJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
