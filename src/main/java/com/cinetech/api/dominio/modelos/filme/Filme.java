package com.cinetech.api.dominio.modelos.filme;

import java.time.LocalDate;
import java.util.Objects;

public class Filme {
    private final FilmeId id;
    private String titulo;
    private String genero;
    private int duracaoMinutos;
    private String idioma;
    private String classificacaoIndicativa;
    private LocalDate dataInicioExibicao;
    private LocalDate dataFimExibicao;
    private String sinopse;
    private double notaMedia;

    public Filme(String titulo, String genero, int duracaoMinutos, String idioma,
                 String classificacaoIndicativa, LocalDate dataInicioExibicao,
                 LocalDate dataFimExibicao, String sinopse) {
        this(FilmeId.novo(), titulo, genero, duracaoMinutos, idioma, classificacaoIndicativa,
                dataInicioExibicao, dataFimExibicao, sinopse, 0.0);
    }

    public Filme(FilmeId id, String titulo, String genero, int duracaoMinutos, String idioma,
                 String classificacaoIndicativa, LocalDate dataInicioExibicao,
                 LocalDate dataFimExibicao, String sinopse, double notaMedia) {
        this.id = Objects.requireNonNull(id, "ID do Filme não pode ser nulo.");
        setTitulo(titulo);
        setGenero(genero);
        setDuracaoMinutos(duracaoMinutos);
        setIdioma(idioma);
        setClassificacaoIndicativa(classificacaoIndicativa);
        this.dataInicioExibicao = Objects.requireNonNull(dataInicioExibicao, "Data de início da exibição não pode ser nula.");
        this.dataFimExibicao = Objects.requireNonNull(dataFimExibicao, "Data de fim da exibição não pode ser nula.");
        validarConsistenciaDatasExibicao();
        setSinopse(sinopse);
        setNotaMedia(notaMedia);
    }

    public FilmeId getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public String getIdioma() { return idioma; }
    public String getClassificacaoIndicativa() { return classificacaoIndicativa; }
    public LocalDate getDataInicioExibicao() { return dataInicioExibicao; }
    public LocalDate getDataFimExibicao() { return dataFimExibicao; }
    public String getSinopse() { return sinopse; }
    public double getNotaMedia() { return notaMedia; }

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

    public void setClassificacaoIndicativa(String classificacaoIndicativa) {
        if (classificacaoIndicativa == null || classificacaoIndicativa.trim().isEmpty()) {
            throw new IllegalArgumentException("Classificação indicativa não pode ser vazia.");
        }
        this.classificacaoIndicativa = classificacaoIndicativa.trim();
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
        this.sinopse = sinopse; // Permitindo nulo ou vazio
    }

    public void setNotaMedia(double notaMedia) { // F8
        if (notaMedia < 0.0 || notaMedia > 5.0) {
            // Permitindo 0 como nota válida, mas não fora do intervalo 0-5
            throw new IllegalArgumentException("Nota média deve estar entre 0.0 e 5.0. Recebido: " + notaMedia);
        }
        this.notaMedia = notaMedia;
    }

    private void validarConsistenciaDatasExibicao() {
        if (this.dataInicioExibicao != null && this.dataFimExibicao != null &&
                this.dataFimExibicao.isBefore(this.dataInicioExibicao)) {
            throw new IllegalStateException("Data de fim da exibição (" + this.dataFimExibicao +
                    ") não pode ser anterior à data de início (" + this.dataInicioExibicao + ").");
        }
    }

    public boolean exibicaoTerminadaEm(LocalDate dataReferencia) { // F3
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (this.dataFimExibicao == null) {
            return false;
        }
        return dataReferencia.isAfter(this.dataFimExibicao);
    }

    public boolean precisaSerRemovidoPorNotaBaixa() { // F8
        return this.notaMedia < 2.5;
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
                '}';
    }
}
