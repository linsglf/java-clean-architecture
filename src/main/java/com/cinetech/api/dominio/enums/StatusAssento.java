package com.cinetech.api.dominio.enums;

public enum StatusAssento {
    DISPONIVEL,         // Livre para seleção
    RESERVADO_TEMP,     // Selecionado pelo cliente, aguardando pagamento (bloqueio temporário)
    OCUPADO_FINAL,      // Ingresso comprado e pagamento confirmado para este assento
    BLOQUEADO           // Indisponível por motivos administrativos ou características da sala (ex: corredor)
}
