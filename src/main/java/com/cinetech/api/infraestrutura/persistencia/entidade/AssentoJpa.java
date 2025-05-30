package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.StatusAssento;
import com.cinetech.api.dominio.enums.TipoAssento;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assento", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sessao_id", "identificador_posicao"}) // Assento é único por sessão e posição
})
public class AssentoJpa {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoJpa sessao; // Referência à SessaoJpa

    @Column(nullable = false, length = 10)
    private String identificadorPosicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAssento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusAssento status;

    @Column(name = "cliente_id_reserva_temp") // ID primitivo do ClienteId
    private UUID clienteIdReservaTemporaria;

    private LocalDateTime timestampExpiracaoReserva;

    public AssentoJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SessaoJpa getSessao() { return sessao; }
    public void setSessao(SessaoJpa sessao) { this.sessao = sessao; }
    public String getIdentificadorPosicao() { return identificadorPosicao; }
    public void setIdentificadorPosicao(String identificadorPosicao) { this.identificadorPosicao = identificadorPosicao; }
    public TipoAssento getTipo() { return tipo; }
    public void setTipo(TipoAssento tipo) { this.tipo = tipo; }
    public StatusAssento getStatus() { return status; }
    public void setStatus(StatusAssento status) { this.status = status; }
    public UUID getClienteIdReservaTemporaria() { return clienteIdReservaTemporaria; }
    public void setClienteIdReservaTemporaria(UUID clienteIdReservaTemporaria) { this.clienteIdReservaTemporaria = clienteIdReservaTemporaria; }
    public LocalDateTime getTimestampExpiracaoReserva() { return timestampExpiracaoReserva; }
    public void setTimestampExpiracaoReserva(LocalDateTime timestampExpiracaoReserva) { this.timestampExpiracaoReserva = timestampExpiracaoReserva; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssentoJpa that = (AssentoJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
