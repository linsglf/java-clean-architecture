package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.repositorios.FilmeRepositorio;
import com.cinetech.api.infraestrutura.persistencia.entidade.FilmeJpa;
import com.cinetech.api.infraestrutura.persistencia.jpa.FilmeJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.mapper.FilmeMapper;
import org.springframework.stereotype.Repository; // Anotação para o bean

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository // <<< ESTA CLASSE É O BEAN
public class FilmeRepositorioJpa implements FilmeRepositorio {

    private final FilmeJpaRepository jpaRepositoryInternal;
    private final FilmeMapper filmeMapper;

    public FilmeRepositorioJpa(FilmeJpaRepository jpaRepositoryInternal, FilmeMapper filmeMapper) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        this.filmeMapper = filmeMapper;
    }

    @Override
    public Filme salvar(Filme filmeDominio) {
        FilmeJpa filmeJpa = filmeMapper.toJpaEntity(filmeDominio);
        FilmeJpa salvoJpa = jpaRepositoryInternal.save(filmeJpa);
        return filmeMapper.toDomainEntity(salvoJpa);
    }

    @Override
    public Optional<Filme> buscarPorId(FilmeId filmeIdDominio) {
        UUID idPrimitivo = filmeMapper.toPrimitiveId(filmeIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(filmeMapper::toDomainEntity);
    }

    @Override
    public Optional<Filme> buscarPorTitulo(String titulo) {
        return jpaRepositoryInternal.findByTitulo(titulo)
                .map(filmeMapper::toDomainEntity);
    }

    @Override
    public List<Filme> buscarTodos() {
        return jpaRepositoryInternal.findAll().stream()
                .map(filmeMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Filme> buscarFilmesEmExibicao(LocalDate dataReferencia) {
        return jpaRepositoryInternal.findFilmesEmExibicaoNaData(dataReferencia).stream()
                .map(filmeMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Filme> buscarFilmesComExibicaoExpiradaParaRemocao(LocalDate dataReferencia) {
        return jpaRepositoryInternal.findByDataFimExibicaoBeforeAndRemovidoDaProgramacaoFalse(dataReferencia).stream()
                .map(filmeMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deletarPorId(FilmeId filmeIdDominio) {
        UUID idPrimitivo = filmeMapper.toPrimitiveId(filmeIdDominio);
        jpaRepositoryInternal.deleteById(idPrimitivo);
    }

    @Override
    public boolean existePorTitulo(String titulo) {
        return jpaRepositoryInternal.existsByTitulo(titulo);
    }
}
