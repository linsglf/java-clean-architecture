package com.cinetech.api.infraestrutura.web.dto.reservaeventos;

import com.cinetech.api.dominio.enums.MetodoPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConfirmarPagamentoReservaRequestDTO {

    @NotBlank(message = "O ID da Reserva de Evento não pode ser vazio.")
    private String reservaEventoId; // UUID da ReservaEvento como String

    @NotNull(message = "O método de pagamento utilizado para confirmação não pode ser nulo.")
    private MetodoPagamento metodoPagamento;

    // Opcional: Se o sistema de pagamento externo fornecer um ID de transação
    // que o cliente precise repassar para confirmação.
    // Para nossa simulação interna, pode não ser estritamente necessário no request,
    // pois o backend vai simular a aprovação. Mas é bom ter em mente.
    private String idTransacaoGatewayCliente; // Ex: ID da transação PIX gerado pelo cliente/app do banco

    // Construtor padrão
    public ConfirmarPagamentoReservaRequestDTO() {
    }

    // Construtor com todos os campos
    public ConfirmarPagamentoReservaRequestDTO(String reservaEventoId, MetodoPagamento metodoPagamento, String idTransacaoGatewayCliente) {
        this.reservaEventoId = reservaEventoId;
        this.metodoPagamento = metodoPagamento;
        this.idTransacaoGatewayCliente = idTransacaoGatewayCliente;
    }

    // Getters e Setters
    public String getReservaEventoId() {
        return reservaEventoId;
    }

    public void setReservaEventoId(String reservaEventoId) {
        this.reservaEventoId = reservaEventoId;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
}