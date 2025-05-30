package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.infraestrutura.persistencia.entidade.FilmeJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface FilmeMapper {

    @Named("filmeIdToUuid")
    default UUID filmeIdToUuid(FilmeId filmeId) {
        return filmeId == null ? null : filmeId.getValor();
    }

    @Named("uuidToFilmeId")
    default FilmeId uuidToFilmeId(UUID uuid) {
        return uuid == null ? null : FilmeId.de(uuid.toString());
    }

    default UUID toPrimitiveId(FilmeId filmeIdVo) {
        return filmeIdVo == null ? null : filmeIdVo.getValor();
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToFilmeId")
    Filme toDomainEntity(FilmeJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "filmeIdToUuid")
    FilmeJpa toJpaEntity(Filme domainEntity);

    List<Filme> toDomainEntityList(List<FilmeJpa> jpaEntityList);
}
