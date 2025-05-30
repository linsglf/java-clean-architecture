package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.pontofidelidade.PontoFidelidade;
import com.cinetech.api.dominio.modelos.pontofidelidade.PontoFidelidadeId;
import com.cinetech.api.infraestrutura.persistencia.entidade.PontoFidelidadeJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PontoFidelidadeMapper {

    @Named("pontoFidelidadeIdToUuid")
    default UUID pontoFidelidadeIdToUuid(PontoFidelidadeId pfId) {
        return pfId == null ? null : pfId.getValorUUID();
    }

    @Named("uuidToPontoFidelidadeId")
    default PontoFidelidadeId uuidToPontoFidelidadeId(UUID uuid) {
        return uuid == null ? null : PontoFidelidadeId.de(uuid.toString());
    }

    default UUID toPrimitiveId(PontoFidelidadeId pfIdVo) {
        return pfIdVo == null ? null : pfIdVo.getValorUUID();
    }

    // Mapeadores para ClienteId e IngressoId
    default UUID clienteIdToUuid(ClienteId clienteId) { return clienteId == null ? null : clienteId.getValor(); }
    default ClienteId uuidToClienteId(UUID uuid) { return uuid == null ? null : ClienteId.de(uuid.toString()); }
    default UUID ingressoIdToUuid(IngressoId ingressoId) { return ingressoId == null ? null : ingressoId.getValor(); }
    default IngressoId uuidToIngressoId(UUID uuid) { return uuid == null ? null : IngressoId.de(uuid.toString()); }


    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToPontoFidelidadeId")
    @Mapping(source = "clienteId", target = "clienteId")
    @Mapping(source = "ingressoOrigemId", target = "ingressoOrigemId")
    PontoFidelidade toDomainEntity(PontoFidelidadeJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "pontoFidelidadeIdToUuid")
    @Mapping(source = "clienteId", target = "clienteId")
    @Mapping(source = "ingressoOrigemId", target = "ingressoOrigemId")
    PontoFidelidadeJpa toJpaEntity(PontoFidelidade domainEntity);

    List<PontoFidelidade> toDomainEntityList(List<PontoFidelidadeJpa> jpaEntityList);
}
