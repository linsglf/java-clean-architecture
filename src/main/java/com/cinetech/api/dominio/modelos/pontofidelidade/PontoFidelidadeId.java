package com.cinetech.api.dominio.modelos.pontofidelidade;

import java.util.Objects;
import java.util.UUID;

public final class PontoFidelidadeId {
    private final UUID valor;

    private PontoFidelidadeId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID do PontoFidelidade não pode ser nulo.");
    }

    public static PontoFidelidadeId novo() {
        return new PontoFidelidadeId(UUID.randomUUID());
    }

    public static PontoFidelidadeId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID do PontoFidelidade não pode ser nula.");
        try {
            return new PontoFidelidadeId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID do PontoFidelidade inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public UUID getValorUUID() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PontoFidelidadeId that = (PontoFidelidadeId) o;
        return valor.equals(that.valor);
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
