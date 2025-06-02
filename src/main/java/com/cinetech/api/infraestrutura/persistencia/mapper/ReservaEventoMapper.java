package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.reservaevento.ReservaEvento;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.modelos.pagamento.PagamentoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.ReservaEventoJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReservaEventoMapper {

    private ReservaEventoMapper() {}

    public static UUID toPrimitiveId(ReservaEventoId idVo) {
        return idVo == null ? null : idVo.getValor();
    }

    public static ReservaEventoId uuidToReservaEventoId(UUID uuid) {
        return uuid == null ? null : ReservaEventoId.de(uuid);
    }

    // Helpers para IDs relacionados
    private static UUID clienteIdToUuid(ClienteId id) { return id == null ? null : id.getValor(); }
    private static ClienteId uuidToClienteId(UUID uuid) { return uuid == null ? null : ClienteId.de(uuid); }
    private static UUID salaIdToUuid(SalaId id) { return id == null ? null : id.getValor(); }
    private static SalaId uuidToSalaId(UUID uuid) { return uuid == null ? null : SalaId.de(uuid); }
    private static UUID pagamentoIdToUuid(PagamentoId id) { return id == null ? null : id.getValor(); }
    private static PagamentoId uuidToPagamentoId(UUID uuid) { return uuid == null ? null : PagamentoId.de(uuid); }


    public static ReservaEventoJpa toJpaEntity(ReservaEvento domainEntity) {
        if (domainEntity == null) return null;
        ReservaEventoJpa jpaEntity = new ReservaEventoJpa();
        if (domainEntity.getId() != null) {
            jpaEntity.setId(toPrimitiveId(domainEntity.getId()));
        }
        if (domainEntity.getClienteId() != null) {
            ClienteJpa clienteRef = new ClienteJpa();
            clienteRef.setId(clienteIdToUuid(domainEntity.getClienteId()));
            jpaEntity.setCliente(clienteRef);
        }
        if (domainEntity.getSalaId() != null) {
            SalaJpa salaRef = new SalaJpa();
            salaRef.setId(salaIdToUuid(domainEntity.getSalaId()));
            jpaEntity.setSala(salaRef);
        }
        jpaEntity.setNomeEvento(domainEntity.getNomeEvento());
        jpaEntity.setDataHoraInicio(domainEntity.getDataHoraInicio());
        jpaEntity.setDataHoraFim(domainEntity.getDataHoraFim());
        jpaEntity.setStatus(domainEntity.getStatus());
        jpaEntity.setValorCobrado(domainEntity.getValorCobrado());
        jpaEntity.setDataSolicitacao(domainEntity.getDataSolicitacao());
        if (domainEntity.getPagamentoId() != null) {
            jpaEntity.setPagamentoId(pagamentoIdToUuid(domainEntity.getPagamentoId()));
        }
        return jpaEntity;
    }

    public static ReservaEvento toDomainEntity(ReservaEventoJpa jpaEntity) {
        if (jpaEntity == null) return null;
        return new ReservaEvento(
                uuidToReservaEventoId(jpaEntity.getId()),
                jpaEntity.getCliente() != null ? uuidToClienteId(jpaEntity.getCliente().getId()) : null,
                jpaEntity.getSala() != null ? uuidToSalaId(jpaEntity.getSala().getId()) : null,
                jpaEntity.getNomeEvento(),
                jpaEntity.getDataHoraInicio(),
                jpaEntity.getDataHoraFim(),
                jpaEntity.getStatus(),
                jpaEntity.getValorCobrado(),
                jpaEntity.getDataSolicitacao(),
                jpaEntity.getPagamentoId() != null ? uuidToPagamentoId(jpaEntity.getPagamentoId()) : null
        );
    }

    public static List<ReservaEvento> toDomainEntityList(List<ReservaEventoJpa> jpaEntityList) {
        if (jpaEntityList == null) return Collections.emptyList();
        return jpaEntityList.stream().map(ReservaEventoMapper::toDomainEntity).collect(Collectors.toList());
    }
}