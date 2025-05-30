package com.cinetech.api.infraestrutura.web.dto.compra;

import java.math.BigDecimal;

public class PrecoCalculadoResponseDTO {
    private BigDecimal precoOriginal;
    private BigDecimal descontoAplicado;
    private BigDecimal precoFinal;
    private String nomePromocaoAplicada;
    private String identificadorAssentoReservado; // Adicionado
    private String mensagemAlerta; // Para F5 - Source 44

    public PrecoCalculadoResponseDTO(BigDecimal precoOriginal, BigDecimal descontoAplicado, BigDecimal precoFinal,
                                     String nomePromocaoAplicada, String identificadorAssentoReservado, String mensagemAlerta) {
        this.precoOriginal = precoOriginal;
        this.descontoAplicado = descontoAplicado;
        this.precoFinal = precoFinal;
        this.nomePromocaoAplicada = nomePromocaoAplicada;
        this.identificadorAssentoReservado = identificadorAssentoReservado;
        this.mensagemAlerta = mensagemAlerta;
    }

    // Getters
    public BigDecimal getPrecoOriginal() { return precoOriginal; }
    public BigDecimal getDescontoAplicado() { return descontoAplicado; }
    public BigDecimal getPrecoFinal() { return precoFinal; }
    public String getNomePromocaoAplicada() { return nomePromocaoAplicada; }
    public String getIdentificadorAssentoReservado() { return identificadorAssentoReservado; }
    public String getMensagemAlerta() { return mensagemAlerta; }
}
