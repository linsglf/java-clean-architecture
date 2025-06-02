package com.cinetech.api.infraestrutura.persistencia.entidade;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ponto_fidelidade")
public class PontoFidelidadeJpa {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // <<< MUDANÇA AQUI
    @JoinColumn(name = "cliente_id", nullable = false)   // <<< MUDANÇA AQUI
    private ClienteJpa cliente;                         // <<< MUDANÇA AQUI

    @Column(nullable = false)
    private int quantidadeOriginal;

    @Column(nullable = false)
    private int quantidadeUtilizada;

    @Column(nullable = false)
    private LocalDateTime dataAquisicao;

    @Column(nullable = false)
    private LocalDate dataExpiracao;

    @Column(name = "ingresso_origem_id")
    private UUID ingressoOrigemId;

    public PontoFidelidadeJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ClienteJpa getCliente() { return cliente; } // <<< MUDANÇA AQUI
    public void setCliente(ClienteJpa cliente) { this.cliente = cliente; } // <<< MUDANÇA AQUI

    public int getQuantidadeOriginal() { return quantidadeOriginal; }
    public void setQuantidadeOriginal(int quantidadeOriginal) { this.quantidadeOriginal = quantidadeOriginal; }
    public int getQuantidadeUtilizada() { return quantidadeUtilizada; }
    public void setQuantidadeUtilizada(int quantidadeUtilizada) { this.quantidadeUtilizada = quantidadeUtilizada; }
    public LocalDateTime getDataAquisicao() { return dataAquisicao; }
    public void setDataAquisicao(LocalDateTime dataAquisicao) { this.dataAquisicao = dataAquisicao; }
    public LocalDate getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDate dataExpiracao) { this.dataExpiracao = dataExpiracao; }
    public UUID getIngressoOrigemId() { return ingressoOrigemId; }
    public void setIngressoOrigemId(UUID ingressoOrigemId) { this.ingressoOrigemId = ingressoOrigemId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PontoFidelidadeJpa that = (PontoFidelidadeJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}