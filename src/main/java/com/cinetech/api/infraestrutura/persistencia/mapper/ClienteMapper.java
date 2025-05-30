package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {CreditoCompensacaoMapper.class, PontoFidelidadeMapper.class})
public interface ClienteMapper {

    @Named("clienteIdToUuid")
    default UUID clienteIdToUuid(ClienteId clienteId) {
        return clienteId == null ? null : clienteId.getValor();
    }

    @Named("uuidToClienteId")
    default ClienteId uuidToClienteId(UUID uuid) {
        return uuid == null ? null : ClienteId.de(uuid.toString());
    }

    default UUID toPrimitiveId(ClienteId clienteIdVo) {
        return clienteIdVo == null ? null : clienteIdVo.getValor();
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToClienteId")
    @Mapping(source = "creditosCompensacaoJpa", target = "creditosCompensacao") // Usa CreditoCompensacaoMapper
    @Mapping(source = "pontosFidelidadeJpa", target = "pontosFidelidade")     // Usa PontoFidelidadeMapper
    Cliente toDomainEntity(ClienteJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "clienteIdToUuid")
    @Mapping(source = "creditosCompensacao", target = "creditosCompensacaoJpa")
    @Mapping(source = "pontosFidelidade", target = "pontosFidelidadeJpa")
    ClienteJpa toJpaEntity(Cliente domainEntity);

    List<Cliente> toDomainEntityList(List<ClienteJpa> jpaEntityList);
}
