package com.cinetech.api.dominio.enums;

public enum PerfilCliente {
    REGULAR("Regular"),
    ESTUDANTE("Estudante"),
    IDOSO("Idoso (60+)"),
    PCD("Pessoa com Deficiência"),
    PROFESSOR_REDE_PUBLICA("Professor da Rede Pública"); // Exemplo de outro perfil comum

    private final String descricao;

    PerfilCliente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
