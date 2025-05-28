package com.cinetech.api.dominio.modelos.cliente;

import com.cinetech.api.dominio.modelos.credito.Credito;
import com.cinetech.api.dominio.enums.PerfilCliente;
import com.cinetech.api.dominio.modelos.pontofidelidade.PontoFidelidade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Cliente {
    private final ClienteId id;
    private String nome;
    private String email;
    private String cpf;
    private PerfilCliente perfil;
    private final List<Credito> creditos;
    private final List<PontoFidelidade> pontosFidelidade;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");

    public Cliente(String nome, String email, String cpf, PerfilCliente perfil) {
        this(ClienteId.novo(), nome, email, cpf, perfil, new ArrayList<>(), new ArrayList<>());
    }

    public Cliente(ClienteId id, String nome, String email, String cpf, PerfilCliente perfil,
                   List<Credito> creditos, List<PontoFidelidade> pontosFidelidade) {
        this.id = Objects.requireNonNull(id, "ID do Cliente não pode ser nulo.");
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        setPerfil(perfil);
        this.creditos = new ArrayList<>(Objects.requireNonNull(creditos, "Lista de créditos não pode ser nula."));
        this.pontosFidelidade = new ArrayList<>(Objects.requireNonNull(pontosFidelidade, "Lista de pontos de fidelidade não pode ser nula."));
    }

    public ClienteId getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public PerfilCliente getPerfil() { return perfil; }
    public List<Credito> getCreditos() { return List.copyOf(creditos); }
    public List<PontoFidelidade> getPontosFidelidade() { return List.copyOf(pontosFidelidade); } // Retorna cópia defensiva

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

    public void adicionarCredito(Credito credito) {
        Objects.requireNonNull(credito, "Crédito não pode ser nulo.");
        if (!credito.getClienteId().equals(this.id)) {
            throw new IllegalArgumentException("O crédito não pertence a este cliente.");
        }
        if (!this.creditos.stream().anyMatch(c -> c.getId().equals(credito.getId()))) {
            this.creditos.add(credito);
        }
    }

    public BigDecimal calcularSaldoCreditosValidos(LocalDateTime dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        return this.creditos.stream()
                .filter(c -> c.estaAtivoEValido(dataReferencia))
                .map(Credito::getValorDisponivel) // Credito deve ter getValorDisponivel()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void adicionarPontosFidelidade(PontoFidelidade pontos) {
        Objects.requireNonNull(pontos, "Objeto PontoFidelidade não pode ser nulo.");
        if (!pontos.getClienteId().equals(this.id)) {
            throw new IllegalArgumentException("Os pontos de fidelidade não pertencem a este cliente.");
        }
        if (!this.pontosFidelidade.stream().anyMatch(p -> p.getId().equals(pontos.getId()))) {
            this.pontosFidelidade.add(pontos);
        }
    }

    public int calcularSaldoPontosFidelidadeValidos(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        return this.pontosFidelidade.stream()
                .filter(p -> p.estaValido(dataReferencia)) // PontoFidelidade deve ter estaValido()
                .mapToInt(PontoFidelidade::getQuantidadeDisponivel) // PontoFidelidade deve ter getQuantidadeDisponivel()
                .sum();
    }

    public void expirarPontosFidelidade(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        this.pontosFidelidade.removeIf(ponto -> !ponto.estaValido(dataReferencia));
    }

    public boolean elegivelParaMeiaEntrada() {
        return this.perfil == PerfilCliente.ESTUDANTE ||
                this.perfil == PerfilCliente.IDOSO ||
                this.perfil == PerfilCliente.PCD ||
                this.perfil == PerfilCliente.PROFESSOR_REDE_PUBLICA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id.equals(cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", perfil=" + perfil +
                '}';
    }
}
