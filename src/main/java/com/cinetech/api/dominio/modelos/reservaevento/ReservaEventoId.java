package com.cinetech.api.dominio.modelos.reservaevento;

import java.util.Objects;
import java.util.UUID;

public final class ReservaEventoId {
    private final UUID valor;

    private ReservaEventoId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID da Reserva de Evento não pode ser nulo.");
    }

    public static ReservaEventoId novo() {
        return new ReservaEventoId(UUID.randomUUID());
    }

    public static ReservaEventoId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID da Reserva de Evento não pode ser nula.");
        try {
            return new ReservaEventoId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID da Reserva de Evento inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public UUID getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservaEventoId that = (ReservaEventoId) o;
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
