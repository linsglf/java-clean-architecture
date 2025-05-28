package com.cinetech.api.dominio.enums;

public enum StatusAvaliacao {
    PENDENTE_MODERACAO, // Nova avaliação, aguardando análise de conteúdo [cite: 21]
    APROVADA,           // Visível para outros usuários [cite: 21]
    REPROVADA_OFENSIVA, // Conteúdo inadequado, não visível [cite: 21]
    OCULTA_PELO_AUTOR   // O próprio usuário decidiu ocultar sua avaliação
}
