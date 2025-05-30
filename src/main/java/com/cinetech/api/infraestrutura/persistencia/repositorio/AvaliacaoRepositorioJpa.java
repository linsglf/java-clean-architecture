package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.enums.StatusAvaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.Avaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.AvaliacaoId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.repositorios.AvaliacaoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.entidade.AvaliacaoJpa;
import com.cinetech.api.infraestrutura.persistencia.jpa.AvaliacaoJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.mapper.AvaliacaoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.ClienteMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.FilmeMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class AvaliacaoRepositorioJpa implements AvaliacaoRepositorio {

    private final AvaliacaoJpaRepository jpaRepositoryInternal;
    private final AvaliacaoMapper avaliacaoMapper;
    // Mappers auxiliares para converter IDs VOs para primitivos para as queries
    private final FilmeMapper filmeMapper;
    private final ClienteMapper clienteMapper;


    public AvaliacaoRepositorioJpa(AvaliacaoJpaRepository jpaRepositoryInternal,
                                   AvaliacaoMapper avaliacaoMapper,
                                   FilmeMapper filmeMapper,
                                   ClienteMapper clienteMapper) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        this.avaliacaoMapper = avaliacaoMapper;
        this.filmeMapper = filmeMapper;
        this.clienteMapper = clienteMapper;
    }

    private Avaliacao mapToDomain(AvaliacaoJpa jpaEntity) {
        if (jpaEntity == null) return null;
        // O AvaliacaoMapper deve ser configurado com 'uses = {FilmeMapper.class, ClienteMapper.class}'
        // para converter FilmeJpa e ClienteJpa referenciados para seus IDs de domínio.
        return avaliacaoMapper.toDomainEntity(jpaEntity);
    }

    private List<Avaliacao> mapToDomainList(List<AvaliacaoJpa> jpaList) {
        if (jpaList == null) return Collections.emptyList();
        return jpaList.stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Avaliacao salvar(Avaliacao avaliacaoDominio) {
        AvaliacaoJpa avaliacaoJpa = avaliacaoMapper.toJpaEntity(avaliacaoDominio);
        AvaliacaoJpa salvaJpa = jpaRepositoryInternal.save(avaliacaoJpa);
        return mapToDomain(salvaJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Avaliacao> buscarPorId(AvaliacaoId avaliacaoIdDominio) {
        UUID idPrimitivo = avaliacaoMapper.toPrimitiveId(avaliacaoIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo).map(this::mapToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarPorFilmeId(FilmeId filmeIdDominio) {
        UUID filmeIdPrimitivo = filmeMapper.toPrimitiveId(filmeIdDominio);
        return mapToDomainList(jpaRepositoryInternal.findByFilme_Id(filmeIdPrimitivo));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Avaliacao> buscarPorClienteEFilme(ClienteId clienteIdDominio, FilmeId filmeIdDominio) {
        UUID clienteIdPrimitivo = clienteMapper.toPrimitiveId(clienteIdDominio);
        UUID filmeIdPrimitivo = filmeMapper.toPrimitiveId(filmeIdDominio);
        return jpaRepositoryInternal.findByCliente_IdAndFilme_Id(clienteIdPrimitivo, filmeIdPrimitivo)
                .map(this::mapToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarAvaliacoesAprovadasPorFilmeId(FilmeId filmeIdDominio) {
        UUID filmeIdPrimitivo = filmeMapper.toPrimitiveId(filmeIdDominio);
        return mapToDomainList(jpaRepositoryInternal.findByFilme_IdAndStatusVisibilidade(filmeIdPrimitivo, StatusAvaliacao.APROVADA));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarPorStatus(StatusAvaliacao status) {
        return mapToDomainList(jpaRepositoryInternal.findByStatusVisibilidade(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarTodas() {
        return mapToDomainList(jpaRepositoryInternal.findAll());
    }
}
