package com.cinetech.api.dominio.ingresso;

import java.util.Objects;
import java.util.UUID;

public final class IngressoId {
    private final UUID valor;

    private IngressoId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID do Ingresso não pode ser nulo.");
    }

    public static IngressoId novo() {
        return new IngressoId(UUID.randomUUID());
    }

    public static IngressoId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID do Ingresso não pode ser nula.");
        try {
            return new IngressoId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID do Ingresso inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public UUID getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngressoId ingressoId = (IngressoId) o;
        return valor.equals(ingressoId.valor);
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
