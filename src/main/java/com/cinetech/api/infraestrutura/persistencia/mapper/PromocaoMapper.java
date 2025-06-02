package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.promocao.Promocao;
import com.cinetech.api.dominio.modelos.promocao.PromocaoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.PromocaoJpa;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PromocaoMapper {

    private PromocaoMapper() {}

    public static UUID toPrimitiveId(PromocaoId promocaoIdVo) {
        return promocaoIdVo == null ? null : promocaoIdVo.getValor();
    }

    public static PromocaoId uuidToPromocaoId(UUID uuid) {
        return uuid == null ? null : PromocaoId.de(uuid);
    }

    public static PromocaoJpa toJpaEntity(Promocao domainEntity) {
        if (domainEntity == null) return null;
        PromocaoJpa jpaEntity = new PromocaoJpa();
        if (domainEntity.getId() != null) {
            jpaEntity.setId(toPrimitiveId(domainEntity.getId()));
        }
        // ... setar todos os outros campos de PromocaoJpa a partir de Promocao ...
        jpaEntity.setNomeDescritivo(domainEntity.getNomeDescritivo());
        jpaEntity.setTipoPromocao(domainEntity.getTipoPromocao());
        jpaEntity.setPercentualDesconto(domainEntity.getPercentualDesconto());
        jpaEntity.setValorDescontoFixo(domainEntity.getValorDescontoFixo());
        jpaEntity.setDiasDaSemanaAplicaveis(domainEntity.getDiasDaSemanaAplicaveis());
        jpaEntity.setHorarioInicioAplicavel(domainEntity.getHorarioInicioAplicavel());
        jpaEntity.setHorarioFimAplicavel(domainEntity.getHorarioFimAplicavel());
        jpaEntity.setPerfisAplicaveis(domainEntity.getPerfisAplicaveis());
        jpaEntity.setAtiva(domainEntity.isAtiva());
        jpaEntity.setDataInicioVigencia(domainEntity.getDataInicioVigencia());
        jpaEntity.setDataFimVigencia(domainEntity.getDataFimVigencia());
        return jpaEntity;
    }

    public static Promocao toDomainEntity(PromocaoJpa jpaEntity) {
        if (jpaEntity == null) return null;
        return new Promocao(
                uuidToPromocaoId(jpaEntity.getId()),
                jpaEntity.getNomeDescritivo(),
                jpaEntity.getTipoPromocao(),
                jpaEntity.getPercentualDesconto(),
                jpaEntity.getValorDescontoFixo(),
                jpaEntity.getDiasDaSemanaAplicaveis(),
                jpaEntity.getHorarioInicioAplicavel(),
                jpaEntity.getHorarioFimAplicavel(),
                jpaEntity.getPerfisAplicaveis(),
                jpaEntity.isAtiva(),
                jpaEntity.getDataInicioVigencia(),
                jpaEntity.getDataFimVigencia()
        );
    }

    public static List<Promocao> toDomainEntityList(List<PromocaoJpa> jpaEntityList) {
        if (jpaEntityList == null) return Collections.emptyList();
        return jpaEntityList.stream().map(PromocaoMapper::toDomainEntity).collect(Collectors.toList());
    }
}