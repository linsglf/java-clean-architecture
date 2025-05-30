package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.ingresso.Ingresso;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.IngressoJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {ClienteMapper.class, SessaoMapper.class, AssentoMapper.class, PromocaoMapper.class})
public interface IngressoMapper {

    @Named("ingressoIdToUuid")
    default UUID ingressoIdToUuid(IngressoId ingressoId) {
        return ingressoId == null ? null : ingressoId.getValor();
    }

    @Named("uuidToIngressoId")
    default IngressoId uuidToIngressoId(UUID uuid) {
        return uuid == null ? null : IngressoId.de(uuid.toString());
    }

    default UUID toPrimitiveId(IngressoId ingressoIdVo) {
        return ingressoIdVo == null ? null : ingressoIdVo.getValor();
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToIngressoId")
    @Mapping(source = "cliente", target = "cliente") // Usa ClienteMapper
    @Mapping(source = "sessao", target = "sessao")   // Usa SessaoMapper (que por sua vez usa FilmeMapper, SalaMapper, AssentoMapper)
    @Mapping(source = "assento", target = "assento") // Usa AssentoMapper
    @Mapping(source = "promocaoAplicadaId", target = "promocaoAplicadaId") // Usa PromocaoMapper para PromocaoId
    Ingresso toDomainEntity(IngressoJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "ingressoIdToUuid")
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(source = "sessao", target = "sessao")
    @Mapping(source = "assento", target = "assento")
    @Mapping(source = "promocaoAplicadaId", target = "promocaoAplicadaId")
    IngressoJpa toJpaEntity(Ingresso domainEntity);

    List<Ingresso> toDomainEntityList(List<IngressoJpa> jpaEntityList);
}
