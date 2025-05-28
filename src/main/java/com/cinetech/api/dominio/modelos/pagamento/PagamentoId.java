package com.cinetech.api.dominio.modelos.pagamento;

import java.util.Objects;
import java.util.UUID;

public final class PagamentoId {
    private final UUID valor;

    private PagamentoId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID do Pagamento não pode ser nulo.");
    }

    public static PagamentoId novo() {
        return new PagamentoId(UUID.randomUUID());
    }

    public static PagamentoId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID do Pagamento não pode ser nula.");
        try {
            return new PagamentoId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID do Pagamento inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public UUID getValor() { return valor; } // Mantido getValor() para consistência com outros IDs

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagamentoId that = (PagamentoId) o;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
