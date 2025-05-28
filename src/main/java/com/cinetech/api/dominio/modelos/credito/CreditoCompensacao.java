package com.cinetech.api.dominio.modelos.credito;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

// Representa um crédito monetário dado ao cliente, ex: por cancelamento de sessão.
public class CreditoCompensacao {
    // Usaremos CreditoId também para esta entidade, pois conceitualmente é um ID de um tipo de 'crédito'.
    // Se quiséssemos IDs estritamente diferentes, criaríamos CreditoCompensacaoId.
    // Por simplicidade, reutilizando CreditoId.
    private final CreditoId id;
    private final ClienteId clienteId;
    private final BigDecimal valorOriginal;
    private BigDecimal valorUtilizado;
    private final LocalDateTime dataEmissao;
    private final LocalDateTime dataValidade; // Pode ter validade
    private boolean ativo;
    private final String motivo; // Ex: "Cancelamento Sessão ID XYZ"
    private final SessaoId sessaoOrigemId; // Sessão que originou o cancelamento e este crédito

    public CreditoCompensacao(ClienteId clienteId, BigDecimal valorOriginal, LocalDateTime dataValidade, String motivo, SessaoId sessaoOrigemId) {
        this(CreditoId.novo(), clienteId, valorOriginal, BigDecimal.ZERO, LocalDateTime.now(), dataValidade, true, motivo, sessaoOrigemId);
    }

    public CreditoCompensacao(CreditoId id, ClienteId clienteId, BigDecimal valorOriginal, BigDecimal valorUtilizado,
                              LocalDateTime dataEmissao, LocalDateTime dataValidade, boolean ativo,
                              String motivo, SessaoId sessaoOrigemId) {
        this.id = Objects.requireNonNull(id, "ID do Crédito de Compensação não pode ser nulo.");
        this.clienteId = Objects.requireNonNull(clienteId, "ID do Cliente não pode ser nulo.");
        if (valorOriginal == null || valorOriginal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor original do crédito de compensação deve ser positivo.");
        }
        this.valorOriginal = valorOriginal;
        if (valorUtilizado == null || valorUtilizado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor utilizado do crédito de compensação não pode ser negativo.");
        }
        if (valorUtilizado.compareTo(valorOriginal) > 0) {
            throw new IllegalArgumentException("Valor utilizado não pode ser maior que o valor original do crédito de compensação.");
        }
        this.valorUtilizado = valorUtilizado;
        this.dataEmissao = Objects.requireNonNull(dataEmissao, "Data de emissão não pode ser nula.");
        this.dataValidade = dataValidade; // Pode ser nulo
        this.ativo = ativo;
        this.motivo = Objects.requireNonNull(motivo, "Motivo do crédito de compensação não pode ser nulo.");
        this.sessaoOrigemId = Objects.requireNonNull(sessaoOrigemId, "ID da Sessão de origem não pode ser nulo.");
    }

    // Getters
    public CreditoId getId() { return id; }
    public ClienteId getClienteId() { return clienteId; }
    public BigDecimal getValorOriginal() { return valorOriginal; }
    public BigDecimal getValorUtilizado() { return valorUtilizado; }
    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public LocalDateTime getDataValidade() { return dataValidade; }
    public boolean isAtivo() { return ativo; }
    public String getMotivo() { return motivo; }
    public SessaoId getSessaoOrigemId() { return sessaoOrigemId; }


    public BigDecimal getValorDisponivel() {
        return this.valorOriginal.subtract(this.valorUtilizado);
    }

    public boolean estaAtivoEValido(LocalDateTime dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (!this.ativo) return false;
        if (this.dataValidade != null && dataReferencia.isAfter(this.dataValidade)) return false;
        return getValorDisponivel().compareTo(BigDecimal.ZERO) > 0;
    }

    public void utilizar(BigDecimal valorAUtilizar) {
        if (!this.ativo) {
            throw new IllegalStateException("Crédito de compensação com ID " + id + " não está ativo.");
        }
        if (valorAUtilizar == null || valorAUtilizar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor a utilizar do crédito de compensação deve ser positivo.");
        }
        if (valorAUtilizar.compareTo(getValorDisponivel()) > 0) {
            throw new IllegalArgumentException("Valor a utilizar (" + valorAUtilizar +
                    ") excede o saldo disponível (" + getValorDisponivel() + ") do crédito de compensação.");
        }
        this.valorUtilizado = this.valorUtilizado.add(valorAUtilizar);
        if (getValorDisponivel().compareTo(BigDecimal.ZERO) == 0) {
            this.ativo = false;
        }
    }

    public void inativar() {
        this.ativo = false;
    }

    @Override
    public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; CreditoCompensacao that = (CreditoCompensacao) o; return id.equals(that.id); }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public String toString() { return "CreditoCompensacao{id=" + id + ", valorDisponivel=" + getValorDisponivel() + ", ativo=" + ativo + "}"; }
}
