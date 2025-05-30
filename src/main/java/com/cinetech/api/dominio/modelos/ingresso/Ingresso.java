package com.cinetech.api.dominio.modelos.ingresso;


import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.modelos.assento.Assento;
import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.modelos.promocao.PromocaoId;
import com.cinetech.api.dominio.modelos.sessao.Sessao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Ingresso {
    // ... (atributos como antes) ...
    private final IngressoId id;
    private final Cliente cliente;
    private final Sessao sessao;
    private final Assento assento;
    private final BigDecimal valorPago;
    private final LocalDateTime dataCompra;
    private final boolean meiaEntradaAplicada;
    private final PromocaoId promocaoAplicadaId;
    private final String codigoValidacao; // Permanece final
    private boolean validadoNaEntrada;

    // Construtor para NOVO ingresso (ID e código de validação são gerados aqui)
    public Ingresso(Cliente cliente, Sessao sessao, Assento assento, BigDecimal valorPago,
                    boolean meiaEntradaAplicada, PromocaoId promocaoAplicadaId) {
        this(IngressoId.novo(), // Gera novo ID
                cliente, sessao, assento, valorPago, LocalDateTime.now(),
                meiaEntradaAplicada, promocaoAplicadaId,
                gerarCodigoValidacaoUnicoInterno(), // Chama o método interno para gerar o código
                false); // validadoNaEntrada
    }

    // Construtor para NOVO ingresso com ID PRÉ-DEFINIDO (código de validação ainda é gerado aqui)
    // Este será usado pelo CompraAplicacao se o IngressoId for gerado antes.
    public Ingresso(IngressoId id, Cliente cliente, Sessao sessao, Assento assento, BigDecimal valorPago,
                    boolean meiaEntradaAplicada, PromocaoId promocaoAplicadaId) {
        this(id, // Usa ID fornecido
                cliente, sessao, assento, valorPago, LocalDateTime.now(), // dataCompra é agora
                meiaEntradaAplicada, promocaoAplicadaId,
                gerarCodigoValidacaoUnicoInterno(), // Gera novo código de validação
                false); // validadoNaEntrada
    }


    // Construtor completo para RECONSTITUIÇÃO (usado por mappers ao ler do banco)
    // Este construtor espera que TODOS os campos sejam fornecidos, incluindo um codigoValidacao existente.
    public Ingresso(IngressoId id, Cliente cliente, Sessao sessao, Assento assento, BigDecimal valorPago,
                    LocalDateTime dataCompra, boolean meiaEntradaAplicada, PromocaoId promocaoAplicadaId,
                    String codigoValidacao, boolean validadoNaEntrada) {
        this.id = Objects.requireNonNull(id, "ID do Ingresso não pode ser nulo.");
        this.cliente = Objects.requireNonNull(cliente, "Cliente do ingresso não pode ser nulo.");
        this.sessao = Objects.requireNonNull(sessao, "Sessão do ingresso não pode ser nula.");
        this.assento = Objects.requireNonNull(assento, "Assento do ingresso não pode ser nulo.");

        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor pago pelo ingresso não pode ser nulo ou negativo.");
        }
        this.valorPago = valorPago;

        this.dataCompra = Objects.requireNonNull(dataCompra, "Data da compra não pode ser nula.");
        this.meiaEntradaAplicada = meiaEntradaAplicada;
        this.promocaoAplicadaId = promocaoAplicadaId;

        if (codigoValidacao == null || codigoValidacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de validação do ingresso não pode ser vazio.");
        }
        this.codigoValidacao = codigoValidacao; // Usa o código fornecido
        this.validadoNaEntrada = validadoNaEntrada;

        if (!this.assento.getSessao().getId().equals(this.sessao.getId())) {
            throw new IllegalStateException("O assento não pertence à sessão informada para o ingresso.");
        }
    }

    // Getters (como antes) ...
    public IngressoId getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Sessao getSessao() { return sessao; }
    public Assento getAssento() { return assento; }
    public BigDecimal getValorPago() { return valorPago; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public boolean isMeiaEntradaAplicada() { return meiaEntradaAplicada; }
    public PromocaoId getPromocaoAplicadaId() { return promocaoAplicadaId; }
    public String getCodigoValidacao() { return codigoValidacao; }
    public boolean isValidadoNaEntrada() { return validadoNaEntrada; }


    // Método de geração de código permanece private static
    private static String gerarCodigoValidacaoUnicoInterno() {
        return "CNT-" + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 12);
    }

    // Métodos de negócio (validarEntrada, ehParaOFilme, podeSerCanceladoPeloCliente) como antes...
    public void validarEntrada() {
        if (this.validadoNaEntrada) {
            throw new IllegalStateException("Ingresso com código " + codigoValidacao + " já foi validado anteriormente.");
        }
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioSessao = sessao.getDataHoraInicio();
        // Calcula o fim estimado da sessão para validação
        LocalDateTime fimSessaoEstimado = inicioSessao.plusMinutes(sessao.getFilme() != null ? sessao.getFilme().getDuracaoMinutos() : 120); // Default se filme for null

        if (agora.isBefore(inicioSessao.minusHours(1)) || agora.isAfter(fimSessaoEstimado.plusMinutes(30))) {
            throw new IllegalStateException("Ingresso não pode ser validado fora do período da sessão. Sessão: " + inicioSessao + ", Agora: " + agora);
        }
        this.validadoNaEntrada = true;
    }

    public boolean ehParaOFilme(FilmeId filmeId) {
        Objects.requireNonNull(filmeId, "ID do Filme para verificação não pode ser nulo.");
        return this.sessao.getFilme().getId().equals(filmeId);
    }

    public boolean podeSerCanceladoPeloCliente(LocalDateTime agora) {
        Objects.requireNonNull(agora, "Data de referência para cancelamento não pode ser nula.");
        if (this.validadoNaEntrada) return false;
        if (agora.isAfter(this.sessao.getDataHoraInicio().minusHours(2))) return false;
        if (this.sessao.getStatus() == StatusSessao.CANCELADA) return false;
        return true;
    }

    // equals, hashCode, toString como antes...
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingresso ingresso = (Ingresso) o;
        return id.equals(ingresso.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Ingresso{" + "id=" + id + ", codigoValidacao='" + codigoValidacao + '\'' + ", validadoNaEntrada=" + validadoNaEntrada +'}';
    }
}
