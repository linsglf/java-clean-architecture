package com.cinetech.api.infraestrutura.web.dto.reservaeventos;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero; // Permite valor zero se o evento for gratuito
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Você pode usar Lombok para getters, setters, etc.
public class CriarReservaEventoRequestDTO {

    @NotBlank(message = "O ID do cliente não pode ser vazio.")
    private String clienteId; // UUID do cliente como String

    @NotBlank(message = "O ID da sala não pode ser vazio.")
    private String salaId; // UUID da sala como String

    @NotBlank(message = "O nome do evento não pode ser vazio.")
    @Size(min = 3, max = 200, message = "Nome do evento deve ter entre 3 e 200 caracteres.")
    private String nomeEvento;

    @NotNull(message = "Data e hora de início não podem ser nulas.")
    @Future(message = "Data e hora de início devem ser no futuro.") // Garante que não é no passado
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "Data e hora de fim não podem ser nulas.")
    @Future(message = "Data e hora de fim devem ser no futuro.")
    private LocalDateTime dataHoraFim;

    @NotNull(message = "Valor estimado não pode ser nulo.")
    @PositiveOrZero(message = "Valor estimado não pode ser negativo.")
    private BigDecimal valorEstimado; // O valor final pode ser diferente, mas este é o inicial

    // Construtor padrão
    public CriarReservaEventoRequestDTO() {
    }

    // Construtor com todos os campos
    public CriarReservaEventoRequestDTO(String clienteId, String salaId, String nomeEvento,
                                        LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                                        BigDecimal valorEstimado) {
        this.clienteId = clienteId;
        this.salaId = salaId;
        this.nomeEvento = nomeEvento;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.valorEstimado = valorEstimado;
    }

    // Getters e Setters
    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getSalaId() {
        return salaId;
    }

    public void setSalaId(String salaId) {
        this.salaId = salaId;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }
}
