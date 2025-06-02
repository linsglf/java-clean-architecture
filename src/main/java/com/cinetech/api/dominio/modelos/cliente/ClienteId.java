package com.cinetech.api.dominio.modelos.cliente;

import java.util.Objects;
import java.util.UUID;

public final class ClienteId {
    private final UUID valor;

    private ClienteId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID do Cliente não pode ser nulo.");
    }
    public static ClienteId novo() { return new ClienteId(UUID.randomUUID()); }
    public static ClienteId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID do Cliente não pode ser nula.");
        try { return new ClienteId(UUID.fromString(valorStr)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("String de ID do Cliente inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    public static ClienteId de(UUID valorUuid) {
        return new ClienteId(valorUuid); // Usa o construtor privado
    }

    public UUID getValor() { return valor; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; ClienteId clienteId = (ClienteId) o; return valor.equals(clienteId.valor); }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
