package com.cinetech.api.dominio.modelos.credito;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Credito {
    private final CreditoId id;
    private final ClienteId clienteId;
    private final BigDecimal valorOriginal;
    private BigDecimal valorUtilizado;
    private final LocalDateTime dataEmissao;
    private final LocalDateTime dataValidade;
    private boolean ativo;
    private final SessaoId sessaoOrigemCancelamentoId;

    public Credito(ClienteId clienteId, BigDecimal valorOriginal, LocalDateTime dataValidade, SessaoId sessaoOrigemCancelamentoId) {
        this(CreditoId.novo(), clienteId, valorOriginal, BigDecimal.ZERO, LocalDateTime.now(), dataValidade, true, sessaoOrigemCancelamentoId);
    }

    public Credito(CreditoId id, ClienteId clienteId, BigDecimal valorOriginal, BigDecimal valorUtilizado,
                   LocalDateTime dataEmissao, LocalDateTime dataValidade, boolean ativo,
                   SessaoId sessaoOrigemCancelamentoId) {

        this.id = Objects.requireNonNull(id, "ID do Crédito não pode ser nulo.");
        this.clienteId = Objects.requireNonNull(clienteId, "ID do Cliente associado ao crédito não pode ser nulo.");

        if (valorOriginal == null || valorOriginal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor original do crédito deve ser positivo.");
        }
        this.valorOriginal = valorOriginal;

        if (valorUtilizado == null || valorUtilizado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor utilizado do crédito não pode ser negativo.");
        }
        if (valorUtilizado.compareTo(valorOriginal) > 0) {
            throw new IllegalArgumentException("Valor utilizado não pode ser maior que o valor original do crédito.");
        }
        this.valorUtilizado = valorUtilizado;

        this.dataEmissao = Objects.requireNonNull(dataEmissao, "Data de emissão do crédito não pode ser nula.");
        this.dataValidade = dataValidade;
        this.ativo = ativo;
        this.sessaoOrigemCancelamentoId = sessaoOrigemCancelamentoId;
    }

    public CreditoId getId() { return id; }
    public ClienteId getClienteId() { return clienteId; }
    public BigDecimal getValorOriginal() { return valorOriginal; }
    public BigDecimal getValorUtilizado() { return valorUtilizado; }
    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public LocalDateTime getDataValidade() { return dataValidade; }
    public boolean isAtivo() { return ativo; }
    public SessaoId getSessaoOrigemCancelamentoId() { return sessaoOrigemCancelamentoId; }

    public BigDecimal getValorDisponivel() {
        return this.valorOriginal.subtract(this.valorUtilizado);
    }

    public boolean estaAtivoEValido(LocalDateTime dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (!this.ativo) {
            return false;
        }
        if (this.dataValidade != null && dataReferencia.isAfter(this.dataValidade)) {
            return false;
        }
        return getValorDisponivel().compareTo(BigDecimal.ZERO) > 0;
    }

    public void utilizar(BigDecimal valorAUtilizar) {
        if (!this.ativo) {
            throw new IllegalStateException("Crédito com ID " + id + " não está ativo.");
        }
        if (valorAUtilizar == null || valorAUtilizar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor a utilizar do crédito deve ser positivo.");
        }
        if (valorAUtilizar.compareTo(getValorDisponivel()) > 0) {
            throw new IllegalArgumentException("Valor a utilizar (" + valorAUtilizar +
                    ") excede o saldo disponível (" + getValorDisponivel() + ") do crédito.");
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credito credito = (Credito) o;
        return id.equals(credito.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Credito{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", valorOriginal=" + valorOriginal +
                ", valorDisponivel=" + getValorDisponivel() +
                ", ativo=" + ativo +
                ", dataValidade=" + dataValidade +
                '}';
    }
}
