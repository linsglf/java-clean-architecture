package com.cinetech.api.dominio.modelos.filme;

import java.time.LocalDate;
import java.util.Objects;

public class Filme {
    private final FilmeId id; // final, setado no construtor
    private String titulo;
    private String genero;
    private int duracaoMinutos;
    private String idioma;
    private String classificacaoEtaria;
    private LocalDate dataInicioExibicao;
    private LocalDate dataFimExibicao;
    private String sinopse;
    private double notaMediaAvaliacao;
    private boolean removidoDaProgramacao;

    // Construtor para criar um NOVO filme (ID e alguns defaults gerados aqui)
    public Filme(String titulo, String genero, int duracaoMinutos, String idioma,
                 String classificacaoEtaria, LocalDate dataInicioExibicao,
                 LocalDate dataFimExibicao, String sinopse) {
        this(FilmeId.novo(), // Gera novo ID
                titulo, genero, duracaoMinutos, idioma, classificacaoEtaria,
                dataInicioExibicao, dataFimExibicao, sinopse,
                0.0, // notaMediaAvaliacao inicial
                false // removidoDaProgramacao inicial
        );
    }

    // Construtor COMPLETO para reconstituição (usado pelo Mapper e persistência)
    // O MapStruct tentará usar este construtor se os nomes dos campos em FilmeJpa
    // (após qualquer conversão de tipo para o ID) corresponderem aos nomes dos parâmetros aqui.
    public Filme(FilmeId id, String titulo, String genero, int duracaoMinutos, String idioma,
                 String classificacaoEtaria, LocalDate dataInicioExibicao,
                 LocalDate dataFimExibicao, String sinopse, double notaMediaAvaliacao, boolean removidoDaProgramacao) {
        this.id = Objects.requireNonNull(id, "ID do Filme não pode ser nulo.");
        // As validações são chamadas pelos setters
        setTitulo(titulo);
        setGenero(genero);
        setDuracaoMinutos(duracaoMinutos);
        setIdioma(idioma);
        setClassificacaoEtaria(classificacaoEtaria);
        // Para datas, a validação de consistência entre elas é feita após ambas serem setadas.
        this.dataInicioExibicao = Objects.requireNonNull(dataInicioExibicao, "Data de início da exibição não pode ser nula.");
        this.dataFimExibicao = Objects.requireNonNull(dataFimExibicao, "Data de fim da exibição não pode ser nula.");
        validarConsistenciaDatasExibicao(); // Valida após ambas estarem setadas
        setSinopse(sinopse);
        setNotaMediaAvaliacao(notaMediaAvaliacao); // Setter valida o intervalo da nota
        this.removidoDaProgramacao = removidoDaProgramacao;
    }

    // Getters
    public FilmeId getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public String getIdioma() { return idioma; }
    public String getClassificacaoEtaria() { return classificacaoEtaria; }
    public LocalDate getDataInicioExibicao() { return dataInicioExibicao; }
    public LocalDate getDataFimExibicao() { return dataFimExibicao; }
    public String getSinopse() { return sinopse; }
    public double getNotaMediaAvaliacao() { return notaMediaAvaliacao; }
    public boolean isRemovidoDaProgramacao() { return removidoDaProgramacao; }

    // Setters com validação
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título do filme não pode ser vazio.");
        }
        this.titulo = titulo.trim();
    }

    public void setGenero(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("Gênero do filme não pode ser vazio.");
        }
        this.genero = genero.trim();
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        if (duracaoMinutos <= 0) {
            throw new IllegalArgumentException("Duração do filme deve ser um valor positivo. Recebido: " + duracaoMinutos);
        }
        this.duracaoMinutos = duracaoMinutos;
    }

    public void setIdioma(String idioma) {
        if (idioma == null || idioma.trim().isEmpty()) {
            throw new IllegalArgumentException("Idioma do filme não pode ser vazio.");
        }
        this.idioma = idioma.trim();
    }

    public void setClassificacaoEtaria(String classificacaoEtaria) {
        if (classificacaoEtaria == null || classificacaoEtaria.trim().isEmpty()) {
            throw new IllegalArgumentException("Classificação etária não pode ser vazia.");
        }
        this.classificacaoEtaria = classificacaoEtaria.trim();
    }

    public void setDataInicioExibicao(LocalDate dataInicioExibicao) {
        this.dataInicioExibicao = Objects.requireNonNull(dataInicioExibicao, "Data de início da exibição não pode ser nula.");
        validarConsistenciaDatasExibicao();
    }

    public void setDataFimExibicao(LocalDate dataFimExibicao) {
        this.dataFimExibicao = Objects.requireNonNull(dataFimExibicao, "Data de fim da exibição não pode ser nula.");
        validarConsistenciaDatasExibicao();
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public void setNotaMediaAvaliacao(double notaMediaAvaliacao) {
        if (notaMediaAvaliacao < 0.0 || notaMediaAvaliacao > 5.0) {
            throw new IllegalArgumentException("Nota média da avaliação deve estar entre 0.0 e 5.0. Recebido: " + notaMediaAvaliacao);
        }
        this.notaMediaAvaliacao = notaMediaAvaliacao;
    }

    // Métodos de Negócio
    private void validarConsistenciaDatasExibicao() {
        if (this.dataInicioExibicao != null && this.dataFimExibicao != null &&
                this.dataFimExibicao.isBefore(this.dataInicioExibicao)) {
            throw new IllegalStateException("Data de fim da exibição (" + this.dataFimExibicao +
                    ") não pode ser anterior à data de início (" + this.dataInicioExibicao + ").");
        }
    }

    public boolean estaEmExibicao(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (isRemovidoDaProgramacao()) {
            return false;
        }
        if (dataInicioExibicao == null || dataFimExibicao == null) {
            return false;
        }
        return !dataReferencia.isBefore(dataInicioExibicao) && !dataReferencia.isAfter(dataFimExibicao);
    }

    public boolean exibicaoTerminadaEm(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (this.dataFimExibicao == null) {
            return false;
        }
        return dataReferencia.isAfter(this.dataFimExibicao);
    }

    public void marcarComoRemovidoDaProgramacao() {
        this.removidoDaProgramacao = true;
    }

    public boolean deveSerRemovidoPorNotaBaixa() {
        return this.notaMediaAvaliacao < 2.5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filme filme = (Filme) o;
        return id.equals(filme.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Filme{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                '}';
    }
}