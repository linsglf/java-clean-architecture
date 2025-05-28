package com.cinetech.api.dominio.sala;

import java.util.Objects;

public class Sala {
    private final SalaId id;
    private String nome;
    private int capacidadeTotal;
    private boolean disponivelParaEventos;

    public Sala(String nome, int capacidadeTotal, boolean disponivelParaEventos) {
        this(SalaId.novo(), nome, capacidadeTotal, disponivelParaEventos);
    }

    public Sala(SalaId id, String nome, int capacidadeTotal, boolean disponivelParaEventos) {
        this.id = Objects.requireNonNull(id, "ID da Sala não pode ser nulo.");
        this.setNome(nome);
        this.setCapacidadeTotal(capacidadeTotal);
        this.disponivelParaEventos = disponivelParaEventos;
    }

    // Getters
    public SalaId getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidadeTotal() { return capacidadeTotal; }
    public boolean isDisponivelParaEventos() { return disponivelParaEventos; }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da sala não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public void setCapacidadeTotal(int capacidadeTotal) {
        if (capacidadeTotal <= 0) {
            throw new IllegalArgumentException("Capacidade total da sala deve ser um valor positivo. Recebido: " + capacidadeTotal);
        }
        this.capacidadeTotal = capacidadeTotal;
    }

    public void setDisponivelParaEventos(boolean disponivelParaEventos) {
        this.disponivelParaEventos = disponivelParaEventos;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sala sala = (Sala) o;
        return id.equals(sala.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sala{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", capacidadeTotal=" + capacidadeTotal +
                ", disponivelParaEventos=" + disponivelParaEventos +
                '}';
    }
}