package com.cinetech.api.dominio.modelos.promocao;

import java.util.Objects;
import java.util.UUID;

public final class PromocaoId {
    private final UUID valor;
    private PromocaoId(UUID valor) { this.valor = Objects.requireNonNull(valor); }
    public static PromocaoId novo() { return new PromocaoId(UUID.randomUUID()); }
    public static PromocaoId de(String valorStr) { /* ... (implementação padrão) ... */
        Objects.requireNonNull(valorStr);
        try { return new PromocaoId(UUID.fromString(valorStr)); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("ID de Promoção inválido", e); }
    }
    public UUID getValor() { return valor; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; PromocaoId that = (PromocaoId) o; return valor.equals(that.valor); }
    @Override public int hashCode() { return Objects.hash(valor); }
    @Override public String toString() { return valor.toString(); }
}
