package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.sala.Sala;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SalaMapper {

    private SalaMapper() {}

    public static UUID toPrimitiveId(SalaId salaIdVo) { // Para ser usado pelos RepositoriosJpa
        return salaIdVo == null ? null : salaIdVo.getValor();
    }

    public static SalaId uuidToSalaId(UUID uuid) { // Para ser usado no toDomainEntity
        return uuid == null ? null : SalaId.de(uuid);
    }

    public static SalaJpa toJpaEntity(Sala domainEntity) {
        if (domainEntity == null) return null;
        SalaJpa jpaEntity = new SalaJpa();
        if (domainEntity.getId() != null) {
            jpaEntity.setId(toPrimitiveId(domainEntity.getId())); // Usa o helper
        }
        jpaEntity.setNome(domainEntity.getNome());
        jpaEntity.setCapacidadeTotal(domainEntity.getCapacidadeTotal());
        jpaEntity.setTipo(domainEntity.getTipo());
        jpaEntity.setDisponivelParaEventos(domainEntity.isDisponivelParaEventos());
        return jpaEntity;
    }

    public static Sala toDomainEntity(SalaJpa jpaEntity) {
        if (jpaEntity == null) return null;
        return new Sala(
                uuidToSalaId(jpaEntity.getId()), // Usa o helper
                jpaEntity.getNome(),
                jpaEntity.getCapacidadeTotal(),
                jpaEntity.getTipo(),
                jpaEntity.isDisponivelParaEventos()
        );
    }

    public static List<Sala> toDomainEntityList(List<SalaJpa> jpaEntityList) {
        if (jpaEntityList == null) return Collections.emptyList();
        return jpaEntityList.stream().map(SalaMapper::toDomainEntity).collect(Collectors.toList());
    }
}