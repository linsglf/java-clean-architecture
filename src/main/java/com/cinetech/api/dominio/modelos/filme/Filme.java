package com.cinetech.api.dominio.modelos.filme;

import java.time.LocalDate;
import java.util.Objects;

public class Filme {
    private final FilmeId id;
    private String titulo; // [cite: 4]
    private String genero; // [cite: 4]
    private int duracaoMinutos; // [cite: 4] ("duração")
    private String idioma; // [cite: 4]
    private String classificacaoEtaria; // [cite: 4]
    private String sinopse; // Atributo comum, embora não listado na tabela, mas implícito para um sistema de cinema.
    private LocalDate dataInicioExibicao; // [cite: 5, 28]
    private LocalDate dataFimExibicao; // [cite: 5, 28]
    private double notaMediaAvaliacao; // Para regra de remoção antecipada [cite: 22]
    private boolean removidoDaProgramacao; // Para F3 [cite: 7]

    public Filme(String titulo, String genero, int duracaoMinutos, String idioma,
                 String classificacaoEtaria, LocalDate dataInicioExibicao,
                 LocalDate dataFimExibicao, String sinopse) {
        this(FilmeId.novo(), titulo, genero, duracaoMinutos, idioma, classificacaoEtaria,
                dataInicioExibicao, dataFimExibicao, sinopse, 0.0, false);
    }

    public Filme(FilmeId id, String titulo, String genero, int duracaoMinutos, String idioma,
                 String classificacaoEtaria, LocalDate dataInicioExibicao,
                 LocalDate dataFimExibicao, String sinopse, double notaMediaAvaliacao, boolean removidoDaProgramacao) {
        this.id = Objects.requireNonNull(id, "ID do Filme não pode ser nulo.");
        setTitulo(titulo);
        setGenero(genero);
        setDuracaoMinutos(duracaoMinutos);
        setIdioma(idioma);
        setClassificacaoEtaria(classificacaoEtaria);
        this.dataInicioExibicao = Objects.requireNonNull(dataInicioExibicao, "Data de início da exibição não pode ser nula.");
        this.dataFimExibicao = Objects.requireNonNull(dataFimExibicao, "Data de fim da exibição não pode ser nula.");
        validarConsistenciaDatasExibicao();
        setSinopse(sinopse);
        setNotaMediaAvaliacao(notaMediaAvaliacao);
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
        this.sinopse = sinopse; // Pode ser nulo ou vazio
    }

    public void setNotaMediaAvaliacao(double notaMediaAvaliacao) { // F8
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

    // rever
    public boolean exibicaoTerminadaEm(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (this.dataFimExibicao == null) { // Se não tem data de fim, não terminou (ou não foi definida)
            return false;
        }
        return dataReferencia.isAfter(this.dataFimExibicao);
    }

    /**
     * Verifica se o filme está em período de exibição na data de referência.
     * Relevante para agendamento de sessão e exibição na listagem de filmes em cartaz.
     * @param dataReferencia A data para a qual verificar a exibição.
     * @return true se o filme está em exibição, false caso contrário.
     */
    public boolean estaEmExibicao(LocalDate dataReferencia) { // <<< MÉTODO ADICIONADO/RESTAURADO
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (isRemovidoDaProgramacao()) { // Se foi removido, não está em exibição
            return false;
        }
        if (dataInicioExibicao == null || dataFimExibicao == null) {
            // Se as datas não estão completamente definidas, não se pode afirmar que está em exibição
            return false;
        }
        // Está em exibição se a data de referência NÃO é ANTES do início E NÃO é DEPOIS do fim.
        // Ou seja, dataReferencia >= dataInicioExibicao AND dataReferencia <= dataFimExibicao
        return !dataReferencia.isBefore(dataInicioExibicao) && !dataReferencia.isAfter(dataFimExibicao);
    }

    public void marcarComoRemovidoDaProgramacao() {
        this.removidoDaProgramacao = true;
    }

    public boolean deveSerRemovidoPorNotaBaixa() {
        return this.notaMediaAvaliacao < 2.5; // [cite: 22]
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
                ", dataFimExibicao=" + dataFimExibicao +
                ", removidoDaProgramacao=" + removidoDaProgramacao +
                '}';
    }
}
