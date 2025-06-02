package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.avaliacao.Avaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.AvaliacaoId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.infraestrutura.persistencia.entidade.AvaliacaoJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa; // Para toJpaEntity
import com.cinetech.api.infraestrutura.persistencia.entidade.FilmeJpa;   // Para toJpaEntity
import org.springframework.context.annotation.Bean;


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AvaliacaoMapper {

    private AvaliacaoMapper() {}

    public static UUID toPrimitiveId(AvaliacaoId avaliacaoIdVo) { // <<< MÉTODO NECESSÁRIO
        return avaliacaoIdVo == null ? null : avaliacaoIdVo.getValor();
    }
    // Os conversores para FilmeId e ClienteId serão chamados a partir de FilmeMapper e ClienteMapper

    public static AvaliacaoJpa toJpaEntity(Avaliacao domainEntity) {
        if (domainEntity == null) return null;
        AvaliacaoJpa jpaEntity = new AvaliacaoJpa();
        if (domainEntity.getId() != null) {
            jpaEntity.setId(toPrimitiveId(domainEntity.getId()));
        }

        if (domainEntity.getFilmeId() != null) {
            FilmeJpa filmeRef = new FilmeJpa();
            filmeRef.setId(FilmeMapper.toPrimitiveId(domainEntity.getFilmeId())); // Chama estático de FilmeMapper
            jpaEntity.setFilme(filmeRef);
        }

        if (domainEntity.getClienteId() != null) {
            ClienteJpa clienteRef = new ClienteJpa();
            clienteRef.setId(ClienteMapper.toPrimitiveId(domainEntity.getClienteId())); // Chama estático de ClienteMapper
            jpaEntity.setCliente(clienteRef);
        }

        jpaEntity.setNota(domainEntity.getNota());
        jpaEntity.setComentario(domainEntity.getComentario());
        jpaEntity.setDataAvaliacao(domainEntity.getDataAvaliacao());
        jpaEntity.setStatusVisibilidade(domainEntity.getStatusVisibilidade());
        return jpaEntity;
    }

    public static Avaliacao toDomainEntity(AvaliacaoJpa jpaEntity) {
        if (jpaEntity == null) return null;

        FilmeId filmeIdDominio = null;
        if (jpaEntity.getFilme() != null && jpaEntity.getFilme().getId() != null) {
            // Chama estático de FilmeMapper para converter UUID para FilmeId
            filmeIdDominio = FilmeMapper.uuidToFilmeId(jpaEntity.getFilme().getId());
        }

        ClienteId clienteIdDominio = null;
        if (jpaEntity.getCliente() != null && jpaEntity.getCliente().getId() != null) {
            // Chama estático de ClienteMapper para converter UUID para ClienteId
            clienteIdDominio = ClienteMapper.uuidToClienteId(jpaEntity.getCliente().getId());
        }

        return new Avaliacao(
                AvaliacaoId.de(jpaEntity.getId()),
                filmeIdDominio,
                clienteIdDominio,
                jpaEntity.getNota(),
                jpaEntity.getComentario(),
                jpaEntity.getDataAvaliacao(),
                jpaEntity.getStatusVisibilidade()
        );
    }

    public static List<Avaliacao> toDomainEntityList(List<AvaliacaoJpa> jpaEntityList) {
        if (jpaEntityList == null) return Collections.emptyList();
        return jpaEntityList.stream().map(AvaliacaoMapper::toDomainEntity).collect(Collectors.toList());
    }
}