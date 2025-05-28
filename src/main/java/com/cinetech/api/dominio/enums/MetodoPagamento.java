package com.cinetech.api.dominio.enums;

public enum MetodoPagamento {
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    PIX,
    CREDITO_INTERNO, // Para F4 (cliente usando saldo de crédito)
    PONTOS_FIDELIDADE // Para F6 (se pontos puderem pagar valor)
}
