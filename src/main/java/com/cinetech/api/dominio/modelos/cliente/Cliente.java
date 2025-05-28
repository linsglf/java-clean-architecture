package com.cinetech.api.dominio.modelos.cliente;

import com.cinetech.api.dominio.enums.PerfilCliente;
import com.cinetech.api.dominio.modelos.credito.CreditoCompensacao;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.pontofidelidade.PontoFidelidade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Cliente {
    private final ClienteId id;
    private String nome;
    private String email;
    private String cpf;
    private PerfilCliente perfil;

    private final List<CreditoCompensacao> creditosCompensacao;
    private final List<PontoFidelidade> pontosFidelidade;

    // ... (padrões de regex e construtores como definidos anteriormente) ...
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");

    public Cliente(String nome, String email, String cpf, PerfilCliente perfil) {
        this(ClienteId.novo(), nome, email, cpf, perfil, new ArrayList<>(), new ArrayList<>());
    }

    public Cliente(ClienteId id, String nome, String email, String cpf, PerfilCliente perfil,
                   List<CreditoCompensacao> creditosCompensacao, List<PontoFidelidade> pontosFidelidade) {
        this.id = Objects.requireNonNull(id, "ID do Cliente não pode ser nulo.");
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        setPerfil(perfil);

        Objects.requireNonNull(creditosCompensacao, "Lista de créditos de compensação não pode ser nula.");
        creditosCompensacao.forEach(credito -> {
            if (!credito.getClienteId().equals(this.id)) {
                throw new IllegalArgumentException("Crédito de compensação com ID " + credito.getId() + " não pertence ao cliente " + this.id);
            }
        });
        this.creditosCompensacao = new ArrayList<>(creditosCompensacao);

        Objects.requireNonNull(pontosFidelidade, "Lista de pontos de fidelidade não pode ser nula.");
        pontosFidelidade.forEach(ponto -> {
            if (!ponto.getClienteId().equals(this.id)) {
                throw new IllegalArgumentException("Ponto de Fidelidade com ID " + ponto.getId() + " não pertence ao cliente " + this.id);
            }
        });
        this.pontosFidelidade = new ArrayList<>(pontosFidelidade);
    }


    // Getters e Setters como definidos anteriormente...
    public ClienteId getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public PerfilCliente getPerfil() { return perfil; }
    public List<CreditoCompensacao> getCreditosCompensacao() { return List.copyOf(creditosCompensacao); }
    public List<PontoFidelidade> getPontosFidelidade() { return List.copyOf(pontosFidelidade); }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty() || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("E-mail do cliente inválido: " + email);
        }
        this.email = email.trim();
    }

    public void setCpf(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("CPF do cliente não pode ser nulo.");
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (!CPF_PATTERN.matcher(cpfLimpo).matches()) {
            throw new IllegalArgumentException("CPF do cliente inválido (deve conter 11 dígitos): " + cpf);
        }
        this.cpf = cpfLimpo;
    }

    public void setPerfil(PerfilCliente perfil) {
        this.perfil = Objects.requireNonNull(perfil, "Perfil do cliente não pode ser nulo.");
    }


    // --- Métodos de Negócio para Crédito de Compensação (F4) ---
    // ... (adicionarCreditoCompensacao, getSaldoTotalCreditosCompensacaoValidos, utilizarCreditosCompensacao) ...
    // Estes métodos já estavam corretos, operando com objetos CreditoCompensacao.
    public void adicionarCreditoCompensacao(CreditoCompensacao credito) {
        Objects.requireNonNull(credito, "Crédito de compensação não pode ser nulo.");
        if (!credito.getClienteId().equals(this.id)) {
            throw new IllegalArgumentException("O crédito de compensação não pertence a este cliente.");
        }
        if (!this.creditosCompensacao.stream().anyMatch(c -> c.getId().equals(credito.getId()))) {
            this.creditosCompensacao.add(credito);
        }
    }

    // ... (outros métodos de CreditoCompensacao) ...


    // --- Métodos de Negócio para Pontos de Fidelidade (F6) ---

    /**
     * Adiciona um novo lote de pontos de fidelidade para o cliente.
     * Este método encapsula a criação do objeto PontoFidelidade.
     * Chamado pelo GestaoPontosFidelidadeService.
     */
    public void adicionarNovosPontos(int quantidadePontos, IngressoId ingressoOrigemId) {
        if (quantidadePontos <= 0) {
            throw new IllegalArgumentException("Quantidade de pontos a adicionar deve ser positiva.");
        }
        // ingressoOrigemId pode ser nulo se os pontos não forem de um ingresso específico.
        PontoFidelidade novosPontos = new PontoFidelidade(this.id, quantidadePontos, ingressoOrigemId);
        this.pontosFidelidade.add(novosPontos); // Adiciona à lista interna
    }

    // O método abaixo pode ser mantido se houver um caso de uso para adicionar um objeto PontoFidelidade já existente
    // (ex: durante a reconstituição do Cliente com seus pontos, embora o construtor já faça isso).
    // Para a concessão de novos pontos, adicionarNovosPontos é mais apropriado.
    public void adicionarPontoFidelidadeExistente(PontoFidelidade pontos) {
        Objects.requireNonNull(pontos, "Objeto PontoFidelidade não pode ser nulo.");
        if (!pontos.getClienteId().equals(this.id)) {
            throw new IllegalArgumentException("Os pontos de fidelidade não pertencem a este cliente.");
        }
        // Evitar duplicatas se o PontoFidelidade já tiver um ID e pudesse ser adicionado mais de uma vez.
        if (!this.pontosFidelidade.stream().anyMatch(p -> p.getId().equals(pontos.getId()))) {
            this.pontosFidelidade.add(pontos);
        }
    }

    public int getSaldoTotalPontosFidelidadeValidos(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência para pontos não pode ser nula.");
        return this.pontosFidelidade.stream()
                .filter(p -> p.estaValido(dataReferencia))
                .mapToInt(PontoFidelidade::getQuantidadeDisponivel)
                .sum();
    }

    public void expirarPontosFidelidadeObsoletos(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência para expirar pontos não pode ser nula.");
        this.pontosFidelidade.removeIf(ponto -> !ponto.estaValido(dataReferencia));
    }

    public void utilizarPontosFidelidade(int quantidadeDesejada, LocalDate dataReferencia) {
        if (quantidadeDesejada <= 0) {
            throw new IllegalArgumentException("Quantidade desejada para utilizar pontos deve ser positiva.");
        }
        Objects.requireNonNull(dataReferencia, "Data de referência para utilizar pontos não pode ser nula.");

        if (getSaldoTotalPontosFidelidadeValidos(dataReferencia) < quantidadeDesejada) {
            throw new IllegalStateException("Saldo de pontos (" + getSaldoTotalPontosFidelidadeValidos(dataReferencia) +
                    ") insuficiente para utilizar " + quantidadeDesejada + " pontos.");
        }

        int pontosUtilizadosTotal = 0;
        List<PontoFidelidade> pontosValidosOrdenados = this.pontosFidelidade.stream()
                .filter(p -> p.estaValido(dataReferencia))
                .sorted((p1, p2) -> p1.getDataExpiracao().compareTo(p2.getDataExpiracao()))
                .collect(Collectors.toList());

        for (PontoFidelidade ponto : pontosValidosOrdenados) {
            int pontosAindaDesejados = quantidadeDesejada - pontosUtilizadosTotal;
            if (pontosAindaDesejados <= 0) break;

            int disponivelNesteLote = ponto.getQuantidadeDisponivel();
            int aUtilizarDesteLote = Math.min(pontosAindaDesejados, disponivelNesteLote);

            ponto.utilizar(aUtilizarDesteLote);
            pontosUtilizadosTotal += aUtilizarDesteLote;
        }
        // Remove os lotes de pontos que foram completamente utilizados
        this.pontosFidelidade.removeIf(ponto -> ponto.getQuantidadeDisponivel() == 0);
    }

    // ... (elegivelParaMeiaEntrada, equals, hashCode, toString como antes) ...
    public boolean elegivelParaMeiaEntrada() {
        return this.perfil == PerfilCliente.ESTUDANTE ||
                this.perfil == PerfilCliente.IDOSO ||
                this.perfil == PerfilCliente.PCD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id.equals(cliente.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Cliente{" + "id=" + id + ", nome='" + nome + '\'' + ", email='" + email + '\'' + '}';
    }
}
