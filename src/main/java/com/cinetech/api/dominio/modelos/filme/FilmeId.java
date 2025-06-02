package com.cinetech.api.dominio.modelos.filme; // Ajuste o pacote para cada XxxxId

import java.util.Objects;
import java.util.UUID;

public final class FilmeId { // Mude "FilmeId" para "SessaoId", "ClienteId", etc.
    private final UUID valor;

    private FilmeId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do ID não pode ser nulo.");
    }

    public static FilmeId novo() {
        return new FilmeId(UUID.randomUUID());
    }

    public static FilmeId de(String valorStr) {
        Objects.requireNonNull(valorStr, "String de ID não pode ser nula.");
        try {
            return new FilmeId(UUID.fromString(valorStr));
        } catch (IllegalArgumentException e) {
            // Adapte a mensagem para cada tipo de ID
            throw new IllegalArgumentException("String de ID do Filme inválida: '" + valorStr + "'. Deve ser um UUID válido.", e);
        }
    }

    // <<< MÉTODO ADICIONADO / AJUSTADO >>>
    public static FilmeId de(UUID valorUuid) {
        return new FilmeId(valorUuid); // Usa o construtor privado
    }

    public UUID getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilmeId that = (FilmeId) o; // Mude "FilmeId" para o tipo correto
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