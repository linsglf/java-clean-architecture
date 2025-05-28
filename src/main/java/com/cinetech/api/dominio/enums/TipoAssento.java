package com.cinetech.api.dominio.enums;

public enum TipoAssento {
    COMUM("Comum"),
    VIP("VIP"),
    PCD("Pessoa com Deficiência"); // Lugares especiais

    private final String descricao;

    TipoAssento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
