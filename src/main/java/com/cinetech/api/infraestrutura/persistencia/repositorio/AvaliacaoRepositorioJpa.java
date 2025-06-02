package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.avaliacao.Avaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.AvaliacaoId;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.enums.StatusAvaliacao;
import com.cinetech.api.dominio.repositorios.AvaliacaoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.AvaliacaoJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.entidade.AvaliacaoJpa;
// Importa a CLASSE Mapper para chamadas estáticas
import com.cinetech.api.infraestrutura.persistencia.mapper.AvaliacaoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.FilmeMapper;   // Para toPrimitiveId de FilmeId
import com.cinetech.api.infraestrutura.persistencia.mapper.ClienteMapper; // Para toPrimitiveId de ClienteId


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
    // REMOVIDA A INJEÇÃO DO MAPPER: private final AvaliacaoMapper avaliacaoMapper;
    // REMOVIDA A INJEÇÃO DOS MAPPERS AUXILIARES:
    // private final FilmeMapper filmeMapper;
    // private final ClienteMapper clienteMapper;


    // Construtor agora só injeta o JpaRepository
    public AvaliacaoRepositorioJpa(AvaliacaoJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        // MAPPERS NÃO SÃO MAIS INJETADOS
    }

    @Override
    @Transactional
    public Avaliacao salvar(Avaliacao avaliacaoDominio) {
        AvaliacaoJpa avaliacaoJpa = AvaliacaoMapper.toJpaEntity(avaliacaoDominio); // Chamada estática
        AvaliacaoJpa salvaJpa = jpaRepositoryInternal.save(avaliacaoJpa);
        return AvaliacaoMapper.toDomainEntity(salvaJpa); // Chamada estática
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Avaliacao> buscarPorId(AvaliacaoId avaliacaoIdDominio) {
        UUID idPrimitivo = AvaliacaoMapper.toPrimitiveId(avaliacaoIdDominio); // Chamada estática
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(AvaliacaoMapper::toDomainEntity); // Referência a método estático
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarPorFilmeId(FilmeId filmeIdDominio) {
        UUID filmeIdPrimitivo = FilmeMapper.toPrimitiveId(filmeIdDominio); // Chamada estática a FilmeMapper
        return jpaRepositoryInternal.findByFilmeId(filmeIdPrimitivo).stream() // Assume que findByFilmeId existe em AvaliacaoJpaRepository
                .map(AvaliacaoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Avaliacao> buscarPorClienteEFilme(ClienteId clienteIdDominio, FilmeId filmeIdDominio) {
        UUID clienteIdPrimitivo = ClienteMapper.toPrimitiveId(clienteIdDominio); // Chamada estática a ClienteMapper
        UUID filmeIdPrimitivo = FilmeMapper.toPrimitiveId(filmeIdDominio);     // Chamada estática a FilmeMapper
        return jpaRepositoryInternal.findByClienteIdAndFilmeId(clienteIdPrimitivo, filmeIdPrimitivo)
                .map(AvaliacaoMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarAvaliacoesAprovadasPorFilmeId(FilmeId filmeIdDominio) {
        UUID filmeIdPrimitivo = FilmeMapper.toPrimitiveId(filmeIdDominio);
        List<AvaliacaoJpa> avaliacoesAprovadasJpa = jpaRepositoryInternal.findByFilmeIdAndStatusVisibilidade(
                filmeIdPrimitivo,
                StatusAvaliacao.APROVADA
        );
        return avaliacoesAprovadasJpa.stream()
                .map(AvaliacaoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarPorStatus(StatusAvaliacao status) {
        return jpaRepositoryInternal.findByStatusVisibilidade(status).stream()
                .map(AvaliacaoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> buscarTodas() {
        return jpaRepositoryInternal.findAll().stream()
                .map(AvaliacaoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}