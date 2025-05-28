package com.cinetech.api.dominio.filme;

import java.util.Objects;
import java.util.UUID;

public final class FilmeId {
    private final UUID valor;

    private FilmeId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID do Filme não pode ser nulo.");
    }

    public static FilmeId novo() {
        return new FilmeId(UUID.randomUUID());
    }

    public static FilmeId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID do Filme não pode ser nula.");
        try {
            return new FilmeId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID do Filme inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public UUID getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilmeId filmeId = (FilmeId) o;
        return valor.equals(filmeId.valor);
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
