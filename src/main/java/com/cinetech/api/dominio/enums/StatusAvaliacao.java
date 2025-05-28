package com.cinetech.api.dominio.enums;

public enum StatusAvaliacao {
    PENDENTE_MODERACAO, // Nova avaliação, aguardando análise de conteúdo
    APROVADA,           // Visível para outros usuários
    REPROVADA_OFENSIVA, // Conteúdo inadequado, não visível
    OCULTA_PELO_USUARIO // O próprio usuário decidiu ocultar sua avaliação
}
