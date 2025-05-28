package com.cinetech.api.dominio.enums;

public enum TipoSala {
    SALA_2D("2D"),
    SALA_3D("3D");
    // Poderíamos adicionar outros tipos como IMAX, VIP aqui se necessário,
    // mas o documento menciona "2D ou 3D" [cite: 4] e assentos VIP/PCD [cite: 4] como características de assento.

    private final String descricao;

    TipoSala(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
