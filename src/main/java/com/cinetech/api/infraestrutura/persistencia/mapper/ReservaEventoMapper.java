package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.pagamento.PagamentoId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEvento;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.infraestrutura.persistencia.entidade.ReservaEventoJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReservaEventoMapper {

    @Named("reservaEventoIdToUuid")
    default UUID reservaEventoIdToUuid(ReservaEventoId reId) {
        return reId == null ? null : reId.getValor();
    }

    @Named("uuidToReservaEventoId")
    default ReservaEventoId uuidToReservaEventoId(UUID uuid) {
        return uuid == null ? null : ReservaEventoId.de(uuid.toString());
    }

    default UUID toPrimitiveId(ReservaEventoId reIdVo) {
        return reIdVo == null ? null : reIdVo.getValor();
    }

    // Mapeadores para IDs
    default UUID clienteIdToUuid(ClienteId clienteId) { return clienteId == null ? null : clienteId.getValor(); }
    default ClienteId uuidToClienteId(UUID uuid) { return uuid == null ? null : ClienteId.de(uuid.toString()); }
    default UUID salaIdToUuid(SalaId salaId) { return salaId == null ? null : salaId.getValor(); }
    default SalaId uuidToSalaId(UUID uuid) { return uuid == null ? null : SalaId.de(uuid.toString()); }
    default UUID pagamentoIdToUuid(PagamentoId pagamentoId) { return pagamentoId == null ? null : pagamentoId.getValor(); }
    default PagamentoId uuidToPagamentoId(UUID uuid) { return uuid == null ? null : PagamentoId.de(uuid.toString()); }


    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToReservaEventoId")
    @Mapping(source = "clienteId", target = "clienteId")
    @Mapping(source = "salaId", target = "salaId")
    @Mapping(source = "pagamentoId", target = "pagamentoId")
    ReservaEvento toDomainEntity(ReservaEventoJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "reservaEventoIdToUuid")
    @Mapping(source = "clienteId", target = "clienteId")
    @Mapping(source = "salaId", target = "salaId")
    @Mapping(source = "pagamentoId", target = "pagamentoId")
    ReservaEventoJpa toJpaEntity(ReservaEvento domainEntity);

    List<ReservaEvento> toDomainEntityList(List<ReservaEventoJpa> jpaEntityList);
}
