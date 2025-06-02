package com.cinetech.api.dominio.modelos.sessao;

import java.util.Objects;
import java.util.UUID;

public final class SessaoId {
    private final UUID valor;
    private SessaoId(UUID valor) { this.valor = Objects.requireNonNull(valor, "Valor do ID da Sessao não pode ser nulo."); }
    public static SessaoId novo() { return new SessaoId(UUID.randomUUID()); }
    public static SessaoId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID da Sessao não pode ser nula.");
        try { return new SessaoId(UUID.fromString(valorStr)); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("String de ID da Sessao inválida: '" + valorStr + "'. Deve ser um UUID válido.", e); }
    }

    public static SessaoId de(UUID valorUuid) {
        return new SessaoId(valorUuid); // Usa o construtor privado
    }

    public UUID getValor() { return valor; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; SessaoId sessaoId = (SessaoId) o; return valor.equals(sessaoId.valor); }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
