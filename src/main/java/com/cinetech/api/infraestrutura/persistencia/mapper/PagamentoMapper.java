package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.pagamento.Pagamento;
import com.cinetech.api.dominio.modelos.pagamento.PagamentoId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.PagamentoJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring") // Não 'uses' outros mappers para os IDs por enquanto
public interface PagamentoMapper {

    @Named("pagamentoIdToUuid")
    default UUID pagamentoIdToUuid(PagamentoId pagamentoId) {
        return pagamentoId == null ? null : pagamentoId.getValor();
    }

    @Named("uuidToPagamentoId")
    default PagamentoId uuidToPagamentoId(UUID uuid) {
        return uuid == null ? null : PagamentoId.de(uuid.toString());
    }

    default UUID toPrimitiveId(PagamentoId pagamentoIdVo) {
        return pagamentoIdVo == null ? null : pagamentoIdVo.getValor();
    }

    // Mapeamento de IngressoId (VO) para UUID (primitivo) e vice-versa
    default UUID ingressoIdToUuid(IngressoId ingressoId) {
        return ingressoId == null ? null : ingressoId.getValor();
    }
    default IngressoId uuidToIngressoId(UUID uuid) {
        return uuid == null ? null : IngressoId.de(uuid.toString());
    }

    // Mapeamento de ReservaEventoId (VO) para UUID (primitivo) e vice-versa
    default UUID reservaEventoIdToUuid(ReservaEventoId reservaEventoId) {
        return reservaEventoId == null ? null : reservaEventoId.getValor();
    }
    default ReservaEventoId uuidToReservaEventoId(UUID uuid) {
        return uuid == null ? null : ReservaEventoId.de(uuid.toString());
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToPagamentoId")
    @Mapping(source = "ingressoId", target = "ingressoId") // MapStruct usará os métodos default acima se os tipos baterem
    @Mapping(source = "reservaEventoId", target = "reservaEventoId")
    Pagamento toDomainEntity(PagamentoJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "pagamentoIdToUuid")
    @Mapping(source = "ingressoId", target = "ingressoId")
    @Mapping(source = "reservaEventoId", target = "reservaEventoId")
    PagamentoJpa toJpaEntity(Pagamento domainEntity);

    List<Pagamento> toDomainEntityList(List<PagamentoJpa> jpaEntityList);
}
