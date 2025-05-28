package com.cinetech.api.dominio.credito;

import java.util.Objects;
import java.util.UUID;

public final class CreditoId {
    private final UUID valor;

    private CreditoId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID do Crédito não pode ser nulo.");
    }

    public static CreditoId novo() {
        return new CreditoId(UUID.randomUUID());
    }

    public static CreditoId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID do Crédito não pode ser nula.");
        try {
            return new CreditoId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID do Crédito inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public UUID getValorUUID() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditoId creditoId = (CreditoId) o;
        return valor.equals(creditoId.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
