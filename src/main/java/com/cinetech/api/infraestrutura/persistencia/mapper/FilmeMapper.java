package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.infraestrutura.persistencia.entidade.FilmeJpa;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FilmeMapper {

    // Construtor privado para impedir instanciação
    private FilmeMapper() {}

    // --- MÉTODOS ESTÁTICOS PÚBLICOS PARA CONVERSÃO DE ID ---
    /**
     * Converte um UUID para um FilmeId (Value Object).
     * Pode ser chamado por outros mappers ou serviços.
     */
    public static FilmeId uuidToFilmeId(UUID uuid) {
        return uuid == null ? null : FilmeId.de(uuid);
    }

    /**
     * Converte um FilmeId (Value Object) para UUID.
     * Pode ser chamado por outros mappers ou serviços.
     * É equivalente ao toPrimitiveId, mas com nome mais simétrico.
     */
    public static UUID filmeIdToUuid(FilmeId filmeId) {
        return filmeId == null ? null : filmeId.getValor();
    }

    /**
     * Helper para obter o valor primitivo do FilmeId.
     * Usado internamente ou por adaptadores de repositório.
     */
    public static UUID toPrimitiveId(FilmeId filmeIdVo) {
        return filmeIdVo == null ? null : filmeIdVo.getValor();
    }
    // --- FIM DOS MÉTODOS DE CONVERSÃO DE ID ---

    public static FilmeJpa toJpaEntity(Filme domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        FilmeJpa jpaEntity = new FilmeJpa();
        if (domainEntity.getId() != null) {
            jpaEntity.setId(filmeIdToUuid(domainEntity.getId())); // Usa o método local estático
        }
        jpaEntity.setTitulo(domainEntity.getTitulo());
        jpaEntity.setGenero(domainEntity.getGenero());
        jpaEntity.setDuracaoMinutos(domainEntity.getDuracaoMinutos());
        jpaEntity.setIdioma(domainEntity.getIdioma());
        jpaEntity.setClassificacaoEtaria(domainEntity.getClassificacaoEtaria());
        jpaEntity.setDataInicioExibicao(domainEntity.getDataInicioExibicao());
        jpaEntity.setDataFimExibicao(domainEntity.getDataFimExibicao());
        jpaEntity.setSinopse(domainEntity.getSinopse());
        jpaEntity.setNotaMediaAvaliacao(domainEntity.getNotaMediaAvaliacao());
        jpaEntity.setRemovidoDaProgramacao(domainEntity.isRemovidoDaProgramacao());
        return jpaEntity;
    }

    public static Filme toDomainEntity(FilmeJpa jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        // Chama o construtor completo da entidade de domínio Filme
        return new Filme(
                uuidToFilmeId(jpaEntity.getId()), // Usa o método local estático
                jpaEntity.getTitulo(),
                jpaEntity.getGenero(),
                jpaEntity.getDuracaoMinutos(),
                jpaEntity.getIdioma(),
                jpaEntity.getClassificacaoEtaria(),
                jpaEntity.getDataInicioExibicao(),
                jpaEntity.getDataFimExibicao(),
                jpaEntity.getSinopse(),
                jpaEntity.getNotaMediaAvaliacao(),
                jpaEntity.isRemovidoDaProgramacao()
        );
    }

    public static List<Filme> toDomainEntityList(List<FilmeJpa> jpaEntityList) {
        if (jpaEntityList == null) {
            return Collections.emptyList();
        }
        return jpaEntityList.stream()
                .map(FilmeMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    public static List<FilmeJpa> toJpaEntityList(List<Filme> domainEntityList) {
        if (domainEntityList == null) {
            return Collections.emptyList();
        }
        return domainEntityList.stream()
                .map(FilmeMapper::toJpaEntity)
                .collect(Collectors.toList());
    }
}