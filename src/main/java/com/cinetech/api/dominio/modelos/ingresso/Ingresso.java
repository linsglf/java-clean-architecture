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
    private final IngressoId id;
    private final Cliente cliente;
    private final Sessao sessao;
    private final Assento assento; // O assento específico para este ingresso
    private final BigDecimal valorPago; // Valor efetivamente pago, já com descontos
    private final LocalDateTime dataCompra;
    private final boolean meiaEntradaAplicada; // Específico para F5
    private final PromocaoId promocaoAplicadaId; // ID da promoção geral aplicada (pode ser a de meia-entrada ou outra)
    private final String codigoValidacao; // Para entrada na sala
    private boolean validadoNaEntrada; // O ingresso já foi usado?

    // Construtor para novo ingresso (geralmente após um pagamento ser confirmado)
    public Ingresso(Cliente cliente, Sessao sessao, Assento assento, BigDecimal valorPago,
                    boolean meiaEntradaAplicada, PromocaoId promocaoAplicadaId) {
        this(IngressoId.novo(), cliente, sessao, assento, valorPago, LocalDateTime.now(),
                meiaEntradaAplicada, promocaoAplicadaId, gerarCodigoValidacaoUnico(), false);
    }

    // Construtor completo para reconstituição
    public Ingresso(IngressoId id, Cliente cliente, Sessao sessao, Assento assento, BigDecimal valorPago,
                    LocalDateTime dataCompra, boolean meiaEntradaAplicada, PromocaoId promocaoAplicadaId,
                    String codigoValidacao, boolean validadoNaEntrada) {
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
        this.promocaoAplicadaId = promocaoAplicadaId; // Pode ser nulo se nenhuma promoção específica foi aplicada além da lógica de meia-entrada.

        if (codigoValidacao == null || codigoValidacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de validação do ingresso não pode ser vazio.");
        }
        this.codigoValidacao = codigoValidacao;
        this.validadoNaEntrada = validadoNaEntrada;

        // Validação de consistência: o assento deve pertencer à sessão
        if (!this.assento.getSessao().getId().equals(this.sessao.getId())) {
            throw new IllegalStateException("O assento ID " + this.assento.getId() + " (identificador '" + this.assento.getIdentificadorPosicao() +
                    "') não pertence à sessão ID " + this.sessao.getId() + " informada para o ingresso.");
        }
        // É esperado que, ao criar um Ingresso, o Assento correspondente já tenha sido marcado como OCUPADO_FINAL
        // pela lógica do Application Service que coordena a compra.
    }

    // Getters
    public IngressoId getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Sessao getSessao() { return sessao; }
    public Assento getAssento() { return assento; }
    public BigDecimal getValorPago() { return valorPago; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public boolean isMeiaEntradaAplicada() { return meiaEntradaAplicada; } // Para F5
    public PromocaoId getPromocaoAplicadaId() { return promocaoAplicadaId; } // Para rastrear outras promoções
    public String getCodigoValidacao() { return codigoValidacao; }
    public boolean isValidadoNaEntrada() { return validadoNaEntrada; }

    // Métodos de Negócio

    private static String gerarCodigoValidacaoUnico() {
        return "CNT-" + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 12);
    }

    /**
     * Marca o ingresso como validado na entrada da sala. (Regra implícita para F8)
     * Impede múltiplas utilizações.
     */
    public void validarEntrada() {
        if (this.validadoNaEntrada) {
            throw new IllegalStateException("Ingresso com código " + codigoValidacao + " já foi validado anteriormente em " + this.dataCompra + ".");
        }
        // Regra de negócio: só pode validar entrada se a sessão estiver próxima de começar ou em andamento.
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioSessao = sessao.getDataHoraInicio();
        LocalDateTime fimSessaoEstimado = inicioSessao.plusMinutes(sessao.getFilme().getDuracaoMinutos()); // Tolerância pode ser adicionada

        // Exemplo de janela de validação: 1 hora antes do início até o fim da sessão
        if (agora.isBefore(inicioSessao.minusHours(1)) || agora.isAfter(fimSessaoEstimado.plusMinutes(30))) { // 30 min de tolerância após o fim
            throw new IllegalStateException("Ingresso não pode ser validado fora do período da sessão. Sessão: " + inicioSessao + ", Agora: " + agora);
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

    /**
     * Verifica se o ingresso pode ser cancelado pelo cliente.
     * Relevante para a regra "Um cliente pode cancelar o ingresso até 2 horas antes da sessão" (Source 18).
     */
    public boolean podeSerCanceladoPeloCliente(LocalDateTime agora) {
        Objects.requireNonNull(agora, "Data de referência para cancelamento não pode ser nula.");
        if (this.validadoNaEntrada) {
            return false; // Não pode cancelar se já foi usado
        }
        // Sessão já ocorreu ou está muito próxima?
        if (agora.isAfter(this.sessao.getDataHoraInicio().minusHours(2))) {
            return false;
        }
        // Outras condições, como status da sessão (se já foi cancelada pelo cinema, não faz sentido o cliente cancelar)
        if (this.sessao.getStatus() == StatusSessao.CANCELADA) {
            return false; // Já foi cancelada pelo sistema, cliente deve receber crédito (F4)
        }
        return true;
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
                ", clienteId=" + (cliente != null ? cliente.getId() : "N/A") +
                ", filmeId=" + (sessao != null && sessao.getFilme() != null ? sessao.getFilme().getId() : "N/A") +
                ", assentoId='" + (assento != null ? assento.getId() : "N/A") + "'" +
                ", valorPago=" + valorPago +
                ", validadoNaEntrada=" + validadoNaEntrada +
                '}';
    }
}
