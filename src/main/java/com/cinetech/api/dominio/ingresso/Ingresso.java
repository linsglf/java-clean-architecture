package com.cinetech.api.dominio.ingresso;


import com.cinetech.api.dominio.assento.Assento;
import com.cinetech.api.dominio.cliente.Cliente;
import com.cinetech.api.dominio.filme.FilmeId;
import com.cinetech.api.dominio.sessao.Sessao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID; // Para gerar código de validação

public class Ingresso {
    private final IngressoId id;
    private final Cliente cliente;
    private final Sessao sessao;
    private final Assento assento; // O assento específico para este ingresso
    private BigDecimal valorPago;
    private final LocalDateTime dataCompra;
    private final boolean meiaEntradaAplicada; // F5
    private final String codigoValidacao; // Para entrada na sala
    private boolean validadoNaEntrada; // O ingresso já foi usado?

    // Construtor para novo ingresso (geralmente após um pagamento ser confirmado)
    public Ingresso(Cliente cliente, Sessao sessao, Assento assento, BigDecimal valorPago, boolean meiaEntradaAplicada) {
        this(IngressoId.novo(), cliente, sessao, assento, valorPago, LocalDateTime.now(), meiaEntradaAplicada,
                gerarCodigoValidacaoUnico(), false);
    }

    // Construtor completo para reconstituição
    public Ingresso(IngressoId id, Cliente cliente, Sessao sessao, Assento assento, BigDecimal valorPago,
                    LocalDateTime dataCompra, boolean meiaEntradaAplicada, String codigoValidacao, boolean validadoNaEntrada) {
        this.id = Objects.requireNonNull(id, "ID do Ingresso não pode ser nulo.");
        this.cliente = Objects.requireNonNull(cliente, "Cliente do ingresso não pode ser nulo.");
        this.sessao = Objects.requireNonNull(sessao, "Sessão do ingresso não pode ser nula.");
        this.assento = Objects.requireNonNull(assento, "Assento do ingresso não pode ser nulo.");

        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor pago pelo ingresso não pode ser nulo ou negativo. Recebido: " + valorPago);
        }
        this.valorPago = valorPago;

        this.dataCompra = Objects.requireNonNull(dataCompra, "Data da compra não pode ser nula.");
        this.meiaEntradaAplicada = meiaEntradaAplicada;

        if (codigoValidacao == null || codigoValidacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de validação do ingresso não pode ser vazio.");
        }
        this.codigoValidacao = codigoValidacao;
        this.validadoNaEntrada = validadoNaEntrada;

        // Validação de consistência: o assento deve pertencer à sessão
        if (!this.assento.getSessao().getId().equals(this.sessao.getId())) {
            throw new IllegalStateException("O assento ID " + this.assento.getId() + " (identificador '" + this.assento.getIdentificador() +
                    "') não pertence à sessão ID " + this.sessao.getId() + " informada para o ingresso.");
        }
        // Outra validação: o assento referenciado pelo ingresso deve estar OCUPADO_FINAL
        // Esta validação é mais uma regra de aplicação ao criar o ingresso,
        // pois o estado do assento é alterado no fluxo de compra.
        // if(this.assento.getStatus() != StatusAssento.OCUPADO_FINAL) {
        //     throw new IllegalStateException("Assento do ingresso não está marcado como ocupado.");
        // }
    }

    // Getters
    public IngressoId getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Sessao getSessao() { return sessao; }
    public Assento getAssento() { return assento; }
    public BigDecimal getValorPago() { return valorPago; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public boolean isMeiaEntradaAplicada() { return meiaEntradaAplicada; }
    public String getCodigoValidacao() { return codigoValidacao; }
    public boolean isValidadoNaEntrada() { return validadoNaEntrada; }

    // Métodos de Negócio

    private static String gerarCodigoValidacaoUnico() {
        // Lógica para gerar um código único, ex: UUID ou um gerador mais robusto.
        return "CIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Marca o ingresso como validado na entrada da sala.
     * Impede múltiplas utilizações.
     */
    public void validarEntrada() {
        if (this.validadoNaEntrada) {
            throw new IllegalStateException("Ingresso com código " + codigoValidacao + " já foi validado anteriormente.");
        }
        // Pode verificar se a data/hora atual é compatível com a sessão
        if (LocalDateTime.now().isAfter(sessao.getDataHoraInicio().plusMinutes(sessao.getFilme().getDuracaoMinutos() + 30))) { // Ex: tolerância de 30 min após fim do filme
            throw new IllegalStateException("Ingresso não pode ser validado: a sessão já terminou há muito tempo.");
        }
        this.validadoNaEntrada = true;
    }

    /**
     * Verifica se este ingresso é para um determinado filme.
     * Relevante para F8 (Avaliação de Filmes).
     */
    public boolean ehParaOFilme(FilmeId filmeId) {
        Objects.requireNonNull(filmeId, "ID do Filme para verificação não pode ser nulo.");
        return this.sessao.getFilme().getId().equals(filmeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingresso ingresso = (Ingresso) o;
        return id.equals(ingresso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ingresso{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getNome() : "N/A") +
                ", filme=" + (sessao != null && sessao.getFilme() != null ? sessao.getFilme().getTitulo() : "N/A") +
                ", assento='" + (assento != null ? assento.getIdentificador() : "N/A") + "'" +
                ", valorPago=" + valorPago +
                ", validado=" + validadoNaEntrada +
                '}';
    }
}
