package com.cinetech.api.infraestrutura.web.dto.compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IngressoResponseDTO {
    private String idIngresso;
    private String nomeCliente;
    private String cpfCliente;
    private String tituloFilme;
    private String nomeSala;
    private String identificadorAssento;
    private LocalDateTime dataHoraSessao;
    private BigDecimal valorPago;
    private boolean meiaEntradaAplicada;
    private String nomePromocaoAplicada;
    private String codigoValidacao;
    private LocalDateTime dataCompra;

    public IngressoResponseDTO(String idIngresso, String nomeCliente, String cpfCliente, String tituloFilme,
                               String nomeSala, String identificadorAssento, LocalDateTime dataHoraSessao,
                               BigDecimal valorPago, boolean meiaEntradaAplicada, String nomePromocaoAplicada,
                               String codigoValidacao, LocalDateTime dataCompra) {
        this.idIngresso = idIngresso;
        this.nomeCliente = nomeCliente;
        this.cpfCliente = cpfCliente;
        this.tituloFilme = tituloFilme;
        this.nomeSala = nomeSala;
        this.identificadorAssento = identificadorAssento;
        this.dataHoraSessao = dataHoraSessao;
        this.valorPago = valorPago;
        this.meiaEntradaAplicada = meiaEntradaAplicada;
        this.nomePromocaoAplicada = nomePromocaoAplicada;
        this.codigoValidacao = codigoValidacao;
        this.dataCompra = dataCompra;
    }

    // Getters
    public String getIdIngresso() { return idIngresso; }
    public String getNomeCliente() { return nomeCliente; }
    public String getCpfCliente() { return cpfCliente; }
    public String getTituloFilme() { return tituloFilme; }
    public String getNomeSala() { return nomeSala; }
    public String getIdentificadorAssento() { return identificadorAssento; }
    public LocalDateTime getDataHoraSessao() { return dataHoraSessao; }
    public BigDecimal getValorPago() { return valorPago; }
    public boolean isMeiaEntradaAplicada() { return meiaEntradaAplicada; }
    public String getNomePromocaoAplicada() { return nomePromocaoAplicada; }
    public String getCodigoValidacao() { return codigoValidacao; }
    public LocalDateTime getDataCompra() { return dataCompra; }
}
