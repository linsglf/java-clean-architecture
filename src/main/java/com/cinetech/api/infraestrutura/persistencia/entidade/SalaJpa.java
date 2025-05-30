package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.TipoSala;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "sala")
public class SalaJpa {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private int capacidadeTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoSala tipo;

    @Column(nullable = false)
    private boolean disponivelParaEventos;

    public SalaJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getCapacidadeTotal() { return capacidadeTotal; }
    public void setCapacidadeTotal(int capacidadeTotal) { this.capacidadeTotal = capacidadeTotal; }
    public TipoSala getTipo() { return tipo; }
    public void setTipo(TipoSala tipo) { this.tipo = tipo; }
    public boolean isDisponivelParaEventos() { return disponivelParaEventos; }
    public void setDisponivelParaEventos(boolean disponivelParaEventos) { this.disponivelParaEventos = disponivelParaEventos; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaJpa salaJpa = (SalaJpa) o;
        return Objects.equals(id, salaJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
