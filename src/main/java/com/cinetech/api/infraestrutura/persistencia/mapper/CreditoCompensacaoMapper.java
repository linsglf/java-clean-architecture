package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.credito.CreditoCompensacao;
import com.cinetech.api.dominio.modelos.credito.CreditoId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId; // VO do domínio
import com.cinetech.api.dominio.modelos.sessao.SessaoId;   // VO do domínio
import com.cinetech.api.infraestrutura.persistencia.entidade.CreditoCompensacaoJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa; // Para criar referência em toJpaEntity

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreditoCompensacaoMapper {

    private CreditoCompensacaoMapper() {}

    public static CreditoCompensacaoJpa toJpaEntity(CreditoCompensacao domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        CreditoCompensacaoJpa jpaEntity = new CreditoCompensacaoJpa();

        if (domainEntity.getId() != null) {
            jpaEntity.setId(domainEntity.getId().getValorUUID());
        }

        // Mapeando ClienteId (VO do domínio) para ClienteJpa (referência na JPA)
        if (domainEntity.getClienteId() != null) {
            ClienteJpa clienteRef = new ClienteJpa(); // Cria uma instância "proxy" de ClienteJpa
            clienteRef.setId(domainEntity.getClienteId().getValor()); // Seta apenas o ID
            jpaEntity.setCliente(clienteRef); // Seta o objeto ClienteJpa na CreditoCompensacaoJpa
        }

        jpaEntity.setValorOriginal(domainEntity.getValorOriginal());
        jpaEntity.setValorUtilizado(domainEntity.getValorUtilizado());
        jpaEntity.setDataEmissao(domainEntity.getDataEmissao());
        jpaEntity.setDataValidade(domainEntity.getDataValidade());
        jpaEntity.setAtivo(domainEntity.isAtivo());
        jpaEntity.setMotivo(domainEntity.getMotivo());

        if (domainEntity.getSessaoOrigemId() != null) {
            jpaEntity.setSessaoOrigemId(domainEntity.getSessaoOrigemId().getValor());
        }
        return jpaEntity;
    }

    public static CreditoCompensacao toDomainEntity(CreditoCompensacaoJpa jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        ClienteId clienteIdDominio = null;
        if (jpaEntity.getCliente() != null && jpaEntity.getCliente().getId() != null) {
            // Obtém o UUID do ClienteJpa e converte para ClienteId (VO do domínio)
            clienteIdDominio = ClienteId.de(jpaEntity.getCliente().getId());
        }

        SessaoId sessaoIdDominio = null;
        if (jpaEntity.getSessaoOrigemId() != null) {
            sessaoIdDominio = SessaoId.de(jpaEntity.getSessaoOrigemId());
        }

        return new CreditoCompensacao(
                CreditoId.de(jpaEntity.getId()),
                clienteIdDominio, // Passa o ClienteId (VO)
                jpaEntity.getValorOriginal(),
                jpaEntity.getValorUtilizado(),
                jpaEntity.getDataEmissao(),
                jpaEntity.getDataValidade(),
                jpaEntity.isAtivo(),
                jpaEntity.getMotivo(),
                sessaoIdDominio
        );
    }

    public static List<CreditoCompensacao> toDomainEntityList(List<CreditoCompensacaoJpa> jpaEntityList) {
        if (jpaEntityList == null) {
            return Collections.emptyList();
        }
        return jpaEntityList.stream()
                .map(CreditoCompensacaoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    public static List<CreditoCompensacaoJpa> toJpaEntityList(List<CreditoCompensacao> domainEntityList) {
        if (domainEntityList == null) {
            return Collections.emptyList();
        }
        return domainEntityList.stream()
                .map(CreditoCompensacaoMapper::toJpaEntity)
                .collect(Collectors.toList());
    }
}