package com.cinetech.api.dominio.enums;

public enum StatusReservaEvento {
    SOLICITADA,            // Cliente demonstrou interesse
    AGUARDANDO_PAGAMENTO,  // Aguardando confirmação do pagamento antecipado
    CONFIRMADA,            // Pagamento efetuado, reserva ativa, sala bloqueada para público
    CANCELADA_PELO_CLIENTE,
    CANCELADA_PELO_SISTEMA,// Ex: falta de pagamento no prazo
    REALIZADA,             // Evento ocorreu
    NAO_REALIZADA          // Evento não ocorreu por algum motivo (no-show, etc.)
}
