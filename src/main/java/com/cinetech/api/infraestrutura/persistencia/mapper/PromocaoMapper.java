package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.promocao.Promocao;
import com.cinetech.api.dominio.modelos.promocao.PromocaoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.PromocaoJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PromocaoMapper {

    @Named("promocaoIdToUuid")
    default UUID promocaoIdToUuid(PromocaoId promocaoId) {
        return promocaoId == null ? null : promocaoId.getValor();
    }

    @Named("uuidToPromocaoId")
    default PromocaoId uuidToPromocaoId(UUID uuid) {
        return uuid == null ? null : PromocaoId.de(uuid.toString());
    }

    default UUID toPrimitiveId(PromocaoId promocaoIdVo) {
        return promocaoIdVo == null ? null : promocaoIdVo.getValor();
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToPromocaoId")
    Promocao toDomainEntity(PromocaoJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "promocaoIdToUuid")
    PromocaoJpa toJpaEntity(Promocao domainEntity);

    List<Promocao> toDomainEntityList(List<PromocaoJpa> jpaEntityList);
}
