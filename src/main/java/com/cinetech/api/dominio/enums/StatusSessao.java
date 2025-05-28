package com.cinetech.api.dominio.enums;

public enum StatusSessao {
    PROGRAMADA,    // Sessão agendada, ingressos podem ou não estar à venda ainda
    ABERTA,        // Ingressos à venda
    LOTADA,        // Todos os assentos vendáveis estão OCUPADO_FINAL ou RESERVADO_TEMP (sem expirar)
    EM_ANDAMENTO,  // Sessão já iniciou
    FINALIZADA,    // Sessão terminou
    CANCELADA      // Sessão foi cancelada
}
