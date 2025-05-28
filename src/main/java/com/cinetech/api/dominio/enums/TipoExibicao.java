package com.cinetech.api.dominio.enums;

public enum TipoExibicao {
    D2("2D"),
    D3("3D");

    private final String descricao;

    TipoExibicao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
