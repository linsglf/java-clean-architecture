package com.cinetech.api.dominio.modelos.pontofidelidade;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class PontoFidelidade {
    private final PontoFidelidadeId id;
    private final ClienteId clienteId;
    private final int quantidadeOriginal;
    private int quantidadeUtilizada;
    private final LocalDateTime dataAquisicao;
    private final LocalDate dataExpiracao;
    private final IngressoId ingressoOrigemId;

    public PontoFidelidade(ClienteId clienteId, int quantidade, IngressoId ingressoOrigemId) {
        this(PontoFidelidadeId.novo(), clienteId, quantidade, 0, LocalDateTime.now(),
                LocalDate.now().plusMonths(6), // F6: validade de 6 meses
                ingressoOrigemId);
    }

    public PontoFidelidade(PontoFidelidadeId id, ClienteId clienteId, int quantidadeOriginal, int quantidadeUtilizada,
                           LocalDateTime dataAquisicao, LocalDate dataExpiracao, IngressoId ingressoOrigemId) {
        this.id = Objects.requireNonNull(id, "ID de PontoFidelidade não pode ser nulo.");
        this.clienteId = Objects.requireNonNull(clienteId, "ID do Cliente associado aos pontos não pode ser nulo.");

        if (quantidadeOriginal <= 0) {
            throw new IllegalArgumentException("Quantidade original de pontos deve ser positiva.");
        }
        this.quantidadeOriginal = quantidadeOriginal;

        if (quantidadeUtilizada < 0) {
            throw new IllegalArgumentException("Quantidade utilizada de pontos não pode ser negativa.");
        }
        if (quantidadeUtilizada > quantidadeOriginal) {
            throw new IllegalArgumentException("Quantidade utilizada não pode ser maior que a quantidade original de pontos.");
        }
        this.quantidadeUtilizada = quantidadeUtilizada;

        this.dataAquisicao = Objects.requireNonNull(dataAquisicao, "Data de aquisição dos pontos não pode ser nula.");
        this.dataExpiracao = Objects.requireNonNull(dataExpiracao, "Data de expiração dos pontos não pode ser nula.");
        this.ingressoOrigemId = ingressoOrigemId; // Pode ser nulo se os pontos não vieram de um ingresso específico
    }

    // Getters
    public PontoFidelidadeId getId() { return id; }
    public ClienteId getClienteId() { return clienteId; }
    public int getQuantidadeOriginal() { return quantidadeOriginal; }
    public int getQuantidadeUtilizada() { return quantidadeUtilizada; }
    public LocalDateTime getDataAquisicao() { return dataAquisicao; }
    public LocalDate getDataExpiracao() { return dataExpiracao; }
    public IngressoId getIngressoOrigemId() { return ingressoOrigemId; }

    public int getQuantidadeDisponivel() {
        return this.quantidadeOriginal - this.quantidadeUtilizada;
    }

    public boolean estaValido(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (dataReferencia.isAfter(this.dataExpiracao)) {
            return false; // Expirado
        }
        return getQuantidadeDisponivel() > 0;
    }

    public void utilizar(int quantidadeAUtilizar) {
        if (quantidadeAUtilizar <= 0) {
            throw new IllegalArgumentException("Quantidade de pontos a utilizar deve ser positiva.");
        }
        if (!estaValido(LocalDate.now())) {
            throw new IllegalStateException("Este lote de pontos com ID " + id + " não está válido para uso (expirado ou zerado).");
        }
        if (quantidadeAUtilizar > getQuantidadeDisponivel()) {
            throw new IllegalArgumentException("Quantidade a utilizar (" + quantidadeAUtilizar +
                    ") excede o saldo disponível (" + getQuantidadeDisponivel() + ") deste lote de pontos.");
        }
        this.quantidadeUtilizada += quantidadeAUtilizar;
    }

    public void marcarComoTotalmenteUtilizado() {
        this.quantidadeUtilizada = this.quantidadeOriginal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PontoFidelidade that = (PontoFidelidade) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PontoFidelidade{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", quantidadeOriginal=" + quantidadeOriginal +
                ", quantidadeDisponivel=" + getQuantidadeDisponivel() +
                ", dataExpiracao=" + dataExpiracao +
                '}';
    }
}
