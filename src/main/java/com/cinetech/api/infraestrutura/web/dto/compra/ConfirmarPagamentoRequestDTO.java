package com.cinetech.api.infraestrutura.web.dto.compra;

import com.cinetech.api.dominio.enums.MetodoPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConfirmarPagamentoRequestDTO {
    @NotBlank(message = "ID do cliente não pode ser vazio.")
    private String clienteId;
    @NotBlank(message = "ID da sessão não pode ser vazio.")
    private String sessaoId;
    @NotBlank(message = "Identificador do assento não pode ser vazio.")
    private String identificadorAssento;
    @NotNull(message = "Método de pagamento não pode ser nulo.")
    private MetodoPagamento metodoPagamento; // Ex: "CARTAO_CREDITO", "PIX"
    // Outros detalhes do pagamento (número do cartão, etc.) viriam aqui, mas omitidos para simplicidade da simulação

    // Getters e Setters
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getSessaoId() { return sessaoId; }
    public void setSessaoId(String sessaoId) { this.sessaoId = sessaoId; }
    public String getIdentificadorAssento() { return identificadorAssento; }
    public void setIdentificadorAssento(String identificadorAssento) { this.identificadorAssento = identificadorAssento; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }
}
