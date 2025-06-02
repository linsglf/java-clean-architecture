package com.cinetech.api.infraestrutura.persistencia.entidade;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "filme")
public class FilmeJpa {

    @Id
    private UUID id; // Corresponde ao FilmeId.valor

    @Column(nullable = false, unique = true, length = 200)
    private String titulo;

    @Column(length = 100)
    private String genero;

    @Column(nullable = false)
    private int duracaoMinutos;

    @Column(length = 50)
    private String idioma;

    @Column(length = 50)
    private String classificacaoEtaria; // Nome igual ao parâmetro do construtor de Filme

    @Lob
    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(nullable = false)
    private LocalDate dataInicioExibicao;

    @Column(nullable = false)
    private LocalDate dataFimExibicao;

    @Column // Removido precision e scale para double
    private double notaMediaAvaliacao;

    @Column(nullable = false)
    private boolean removidoDaProgramacao;

    public FilmeJpa() { // Construtor padrão exigido pelo JPA
    }

    // Getters e Setters para todos os campos (necessários para JPA e MapStruct)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public String getClassificacaoEtaria() { return classificacaoEtaria; }
    public void setClassificacaoEtaria(String classificacaoEtaria) { this.classificacaoEtaria = classificacaoEtaria; }
    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }
    public LocalDate getDataInicioExibicao() { return dataInicioExibicao; }
    public void setDataInicioExibicao(LocalDate dataInicioExibicao) { this.dataInicioExibicao = dataInicioExibicao; }
    public LocalDate getDataFimExibicao() { return dataFimExibicao; }
    public void setDataFimExibicao(LocalDate dataFimExibicao) { this.dataFimExibicao = dataFimExibicao; }
    public double getNotaMediaAvaliacao() { return notaMediaAvaliacao; }
    public void setNotaMediaAvaliacao(double notaMediaAvaliacao) { this.notaMediaAvaliacao = notaMediaAvaliacao; }
    public boolean isRemovidoDaProgramacao() { return removidoDaProgramacao; }
    public void setRemovidoDaProgramacao(boolean removidoDaProgramacao) { this.removidoDaProgramacao = removidoDaProgramacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilmeJpa filmeJpa = (FilmeJpa) o;
        return Objects.equals(id, filmeJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}