package com.cinetech.api.dominio.modelos.sala;

import java.util.Objects;
import java.util.UUID;

public final class SalaId {
    private final UUID valor;

    private SalaId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID da Sala não pode ser nulo.");
    }

    public static SalaId novo() {
        return new SalaId(UUID.randomUUID());
    }

    public static SalaId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID da Sala não pode ser nula.");
        try {
            return new SalaId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID da Sala inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public static SalaId de(UUID valorUuid) {
        return new SalaId(valorUuid); // Usa o construtor privado
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaId salaId = (SalaId) o;
        return valor.equals(salaId.valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor.toString(); }
}
