package com.cinetech.api.dominio.modelos.assento;

import java.util.Objects;
import java.util.UUID;

public final class AssentoId {
    private final UUID valor;
    private AssentoId(UUID valor) { this.valor = Objects.requireNonNull(valor, "Valor do ID do Assento não pode ser nulo."); }
    public static AssentoId novo() { return new AssentoId(UUID.randomUUID()); }
    public static AssentoId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID do Assento não pode ser nula.");
        try { return new AssentoId(UUID.fromString(valorStr)); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("String de ID do Assento inválida: '" + valorStr + "'. Deve ser um UUID válido.", e); }
    }

    public static AssentoId de(UUID valorUuid) {
        return new AssentoId(valorUuid); // Usa o construtor privado
    }

    public UUID getValor() { return valor; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; AssentoId assentoId = (AssentoId) o; return valor.equals(assentoId.valor); }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
