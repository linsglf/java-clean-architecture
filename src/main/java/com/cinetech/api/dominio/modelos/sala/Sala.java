package com.cinetech.api.dominio.modelos.sala;

import com.cinetech.api.dominio.enums.TipoSala;

import java.util.Objects;

public class Sala {
    private final SalaId id;
    private String nome;
    private int capacidadeTotal; // [cite: 13]
    private TipoSala tipo; // 2D ou 3D [cite: 4, 13]
    private boolean disponivelParaEventos; // [cite: 25] (implícito)
    // A descrição de "possuir assentos comuns, VIP ou PCD" [cite: 4, 13]
    // será refletida na configuração dos Assentos dentro de uma Sessao nesta Sala,
    // ou em um layout padrão da sala. Para o modelo da Sala em si, a capacidade é o principal.

    // Construtor para nova sala
    public Sala(String nome, int capacidadeTotal, TipoSala tipo, boolean disponivelParaEventos) {
        this(SalaId.novo(), nome, capacidadeTotal, tipo, disponivelParaEventos);
    }

    // Construtor principal para criação/reconstituição
    public Sala(SalaId id, String nome, int capacidadeTotal, TipoSala tipo, boolean disponivelParaEventos) {
        this.id = Objects.requireNonNull(id, "ID da Sala não pode ser nulo.");
        setNome(nome);
        setCapacidadeTotal(capacidadeTotal);
        setTipo(tipo);
        this.disponivelParaEventos = disponivelParaEventos;
    }

    // Getters
    public SalaId getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidadeTotal() { return capacidadeTotal; }
    public TipoSala getTipo() { return tipo; }
    public boolean isDisponivelParaEventos() { return disponivelParaEventos; }

    // Setters com validação
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

    public void setTipo(TipoSala tipo) {
        this.tipo = Objects.requireNonNull(tipo, "Tipo da sala não pode ser nulo.");
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
                ", tipo=" + tipo +
                ", disponivelParaEventos=" + disponivelParaEventos +
                '}';
    }
}