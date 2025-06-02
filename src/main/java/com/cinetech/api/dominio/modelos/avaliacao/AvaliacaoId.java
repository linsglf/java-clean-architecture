package com.cinetech.api.dominio.modelos.avaliacao;

import java.util.Objects;
import java.util.UUID;

public final class AvaliacaoId {
    private final UUID valor;

    private AvaliacaoId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID da Avaliação não pode ser nulo.");
    }

    public static AvaliacaoId novo() {
        return new AvaliacaoId(UUID.randomUUID());
    }

    public static AvaliacaoId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID da Avaliação não pode ser nula.");
        try {
            return new AvaliacaoId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID da Avaliação inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public static AvaliacaoId de(UUID valorUuid) {
        return new AvaliacaoId(valorUuid); // Usa o construtor privado
    }

    public UUID getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvaliacaoId that = (AvaliacaoId) o;
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
