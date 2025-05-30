package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.infraestrutura.persistencia.entidade.FilmeJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FilmeJpaRepository extends JpaRepository<FilmeJpa, UUID> {
    Optional<FilmeJpa> findByTitulo(String titulo);
    boolean existsByTitulo(String titulo);

    // Para buscar filmes em exibição em uma data específica
    @Query("SELECT f FROM FilmeJpa f WHERE f.removidoDaProgramacao = false AND :dataReferencia BETWEEN f.dataInicioExibicao AND f.dataFimExibicao")
    List<FilmeJpa> findFilmesEmExibicaoNaData(@Param("dataReferencia") LocalDate dataReferencia);

    // Para buscar filmes cuja data de fim de exibição já passou e não foram removidos
    List<FilmeJpa> findByDataFimExibicaoBeforeAndRemovidoDaProgramacaoFalse(LocalDate dataReferencia);
}
