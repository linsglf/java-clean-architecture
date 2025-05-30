package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.sala.Sala;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SalaMapper {

    @Named("salaIdToUuid")
    default UUID salaIdToUuid(SalaId salaId) {
        return salaId == null ? null : salaId.getValor();
    }

    @Named("uuidToSalaId")
    default SalaId uuidToSalaId(UUID uuid) {
        return uuid == null ? null : SalaId.de(uuid.toString());
    }

    default UUID toPrimitiveId(SalaId salaIdVo) {
        return salaIdVo == null ? null : salaIdVo.getValor();
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToSalaId")
    Sala toDomainEntity(SalaJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "salaIdToUuid")
    SalaJpa toJpaEntity(Sala domainEntity);

    List<Sala> toDomainEntityList(List<SalaJpa> jpaEntityList);
}
