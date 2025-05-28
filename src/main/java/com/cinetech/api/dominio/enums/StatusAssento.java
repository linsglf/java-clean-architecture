package com.cinetech.api.dominio.enums;

public enum StatusAssento {
    DISPONIVEL,         // Livre para seleção [F1]
    RESERVADO_TEMP,     // Selecionado pelo cliente, aguardando pagamento (bloqueio temporário) [F1]
    OCUPADO_FINAL,      // Ingresso comprado e pagamento confirmado para este assento [F1]
    BLOQUEADO           // Indisponível por motivos administrativos ou características da sala (ex: corredor, manutenção)
}
