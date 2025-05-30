package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.credito.CreditoCompensacao;
import com.cinetech.api.dominio.modelos.credito.CreditoId;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.CreditoCompensacaoJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring") // uses = {ClienteMapper.class, SessaoMapper.class} implicitamente para os IDs
public interface CreditoCompensacaoMapper {

    // Reutilizando os mapeadores de ID de outros mappers ou definindo aqui
    @Named("creditoIdToUuid")
    default UUID creditoIdToUuid(CreditoId creditoId) {
        return creditoId == null ? null : creditoId.getValorUUID();
    }

    @Named("uuidToCreditoId")
    default CreditoId uuidToCreditoId(UUID uuid) {
        return uuid == null ? null : CreditoId.de(uuid.toString());
    }

    default UUID toPrimitiveId(CreditoId creditoIdVo) {
        return creditoIdVo == null ? null : creditoIdVo.getValorUUID();
    }

    // Mapeadores para ClienteId e SessaoId (assumindo que são VOs simples com UUID)
    default UUID clienteIdToUuid(ClienteId clienteId) { return clienteId == null ? null : clienteId.getValor(); }
    default ClienteId uuidToClienteId(UUID uuid) { return uuid == null ? null : ClienteId.de(uuid.toString()); }
    default UUID sessaoIdToUuid(SessaoId sessaoId) { return sessaoId == null ? null : sessaoId.getValor(); }
    default SessaoId uuidToSessaoId(UUID uuid) { return uuid == null ? null : SessaoId.de(uuid.toString()); }


    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToCreditoId")
    @Mapping(source = "clienteId", target = "clienteId") // Mapeia UUID para ClienteId
    @Mapping(source = "sessaoOrigemId", target = "sessaoOrigemId") // Mapeia UUID para SessaoId
    CreditoCompensacao toDomainEntity(CreditoCompensacaoJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "creditoIdToUuid")
    @Mapping(source = "clienteId", target = "clienteId") // Mapeia ClienteId para UUID
    @Mapping(source = "sessaoOrigemId", target = "sessaoOrigemId") // Mapeia SessaoId para UUID
    CreditoCompensacaoJpa toJpaEntity(CreditoCompensacao domainEntity);

    List<CreditoCompensacao> toDomainEntityList(List<CreditoCompensacaoJpa> jpaEntityList);
}
