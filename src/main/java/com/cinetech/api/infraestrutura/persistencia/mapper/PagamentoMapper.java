package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.pagamento.Pagamento;
import com.cinetech.api.dominio.modelos.pagamento.PagamentoId;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.PagamentoJpa;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PagamentoMapper {

    private PagamentoMapper() {}

    public static UUID toPrimitiveId(PagamentoId pagamentoIdVo) {
        return pagamentoIdVo == null ? null : pagamentoIdVo.getValor();
    }

    public static PagamentoId uuidToPagamentoId(UUID uuid) {
        return uuid == null ? null : PagamentoId.de(uuid);
    }

    // Helpers estáticos para IDs relacionados (se não quiser chamar XxxxMapper.toPrimitiveId)
    private static UUID ingressoIdToUuid(IngressoId ingressoId) {
        return ingressoId == null ? null : ingressoId.getValor();
    }
    private static IngressoId uuidToIngressoId(UUID uuid) {
        return uuid == null ? null : IngressoId.de(uuid);
    }
    private static UUID reservaEventoIdToUuid(ReservaEventoId reservaEventoId) {
        return reservaEventoId == null ? null : reservaEventoId.getValor();
    }
    private static ReservaEventoId uuidToReservaEventoId(UUID uuid) {
        return uuid == null ? null : ReservaEventoId.de(uuid);
    }

    public static PagamentoJpa toJpaEntity(Pagamento domainEntity) {
        if (domainEntity == null) return null;
        PagamentoJpa jpaEntity = new PagamentoJpa();
        if (domainEntity.getId() != null) {
            jpaEntity.setId(toPrimitiveId(domainEntity.getId()));
        }
        jpaEntity.setValor(domainEntity.getValor());
        jpaEntity.setMetodoPagamento(domainEntity.getMetodoPagamento());
        jpaEntity.setStatus(domainEntity.getStatus());
        jpaEntity.setDataCriacao(domainEntity.getDataCriacao());
        jpaEntity.setDataAtualizacao(domainEntity.getDataAtualizacao());
        jpaEntity.setIdTransacaoGateway(domainEntity.getIdTransacaoGateway());
        if (domainEntity.getIngressoId() != null) {
            jpaEntity.setIngressoId(ingressoIdToUuid(domainEntity.getIngressoId()));
        }
        if (domainEntity.getReservaEventoId() != null) {
            jpaEntity.setReservaEventoId(reservaEventoIdToUuid(domainEntity.getReservaEventoId()));
        }
        return jpaEntity;
    }

    public static Pagamento toDomainEntity(PagamentoJpa jpaEntity) {
        if (jpaEntity == null) return null;
        return new Pagamento(
                uuidToPagamentoId(jpaEntity.getId()),
                jpaEntity.getValor(),
                jpaEntity.getMetodoPagamento(),
                jpaEntity.getStatus(),
                jpaEntity.getDataCriacao(),
                jpaEntity.getDataAtualizacao(),
                jpaEntity.getIdTransacaoGateway(),
                jpaEntity.getIngressoId() != null ? uuidToIngressoId(jpaEntity.getIngressoId()) : null,
                jpaEntity.getReservaEventoId() != null ? uuidToReservaEventoId(jpaEntity.getReservaEventoId()) : null
        );
    }

    public static List<Pagamento> toDomainEntityList(List<PagamentoJpa> jpaEntityList) {
        if (jpaEntityList == null) return Collections.emptyList();
        return jpaEntityList.stream().map(PagamentoMapper::toDomainEntity).collect(Collectors.toList());
    }
}