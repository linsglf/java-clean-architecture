package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.repositorios.FilmeRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.FilmeJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.entidade.FilmeJpa;
// Importe a CLASSE do mapper manual
import com.cinetech.api.infraestrutura.persistencia.mapper.FilmeMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FilmeRepositorioJpa implements FilmeRepositorio {

    private final FilmeJpaRepository jpaRepositoryInternal;
    // NÃO HÁ MAIS INJEÇÃO DO MAPPER AQUI

    public FilmeRepositorioJpa(FilmeJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        // O MAPPER NÃO É MAIS INJETADO
    }

    @Override
    public Filme salvar(Filme filmeDominio) {
        FilmeJpa filmeJpa = FilmeMapper.toJpaEntity(filmeDominio); // Chamada estática
        FilmeJpa salvoJpa = jpaRepositoryInternal.save(filmeJpa);
        return FilmeMapper.toDomainEntity(salvoJpa); // Chamada estática
    }

    @Override
    public Optional<Filme> buscarPorId(FilmeId filmeIdDominio) {
        UUID idPrimitivo = FilmeMapper.toPrimitiveId(filmeIdDominio); // Chamada estática
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(FilmeMapper::toDomainEntity); // Chamada estática (referência de método)
    }

    @Override
    public Optional<Filme> buscarPorTitulo(String titulo) {
        return jpaRepositoryInternal.findByTitulo(titulo)
                .map(FilmeMapper::toDomainEntity);
    }

    @Override
    public List<Filme> buscarTodos() {
        return jpaRepositoryInternal.findAll().stream()
                .map(FilmeMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Filme> buscarFilmesEmExibicao(LocalDate dataReferencia) {
        return jpaRepositoryInternal.findFilmesEmExibicaoNaData(dataReferencia).stream()
                .map(FilmeMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Filme> buscarFilmesComExibicaoExpiradaParaRemocao(LocalDate dataReferencia) {
        return jpaRepositoryInternal.findByDataFimExibicaoBeforeAndRemovidoDaProgramacaoFalse(dataReferencia).stream()
                .map(FilmeMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deletarPorId(FilmeId filmeIdDominio) {
        UUID idPrimitivo = FilmeMapper.toPrimitiveId(filmeIdDominio);
        jpaRepositoryInternal.deleteById(idPrimitivo);
    }

    @Override
    public boolean existePorTitulo(String titulo) {
        return jpaRepositoryInternal.existsByTitulo(titulo);
    }
}