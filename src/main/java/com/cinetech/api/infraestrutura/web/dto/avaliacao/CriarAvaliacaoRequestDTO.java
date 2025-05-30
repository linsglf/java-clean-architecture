package com.cinetech.api.infraestrutura.web.dto.avaliacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

// Você pode usar Lombok para getters, setters, etc.
public class CriarAvaliacaoRequestDTO {

    @NotBlank(message = "O ID do filme não pode ser vazio.")
    private String filmeId; // UUID do filme como String

    @NotBlank(message = "O ID do cliente não pode ser vazio.")
    private String clienteId; // UUID do cliente como String

    @NotNull(message = "A nota não pode ser nula.")
    @Min(value = 1, message = "A nota mínima é 1.")
    @Max(value = 5, message = "A nota máxima é 5.")
    private Integer nota;

    @Size(max = 1000, message = "O comentário não pode exceder 1000 caracteres.")
    private String comentario; // Comentário é opcional, mas se fornecido tem limite de tamanho

    // Construtor padrão (necessário para algumas bibliotecas de desserialização como Jackson)
    public CriarAvaliacaoRequestDTO() {
    }

    // Construtor com todos os campos (útil para testes ou criação manual)
    public CriarAvaliacaoRequestDTO(String filmeId, String clienteId, Integer nota, String comentario) {
        this.filmeId = filmeId;
        this.clienteId = clienteId;
        this.nota = nota;
        this.comentario = comentario;
    }

    // Getters e Setters
    public String getFilmeId() {
        return filmeId;
    }

    public void setFilmeId(String filmeId) {
        this.filmeId = filmeId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}