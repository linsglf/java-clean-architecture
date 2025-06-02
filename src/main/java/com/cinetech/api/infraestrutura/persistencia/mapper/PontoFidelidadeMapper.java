package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.pontofidelidade.PontoFidelidade;
import com.cinetech.api.dominio.modelos.pontofidelidade.PontoFidelidadeId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;    // VO do domínio
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;  // VO do domínio
import com.cinetech.api.infraestrutura.persistencia.entidade.PontoFidelidadeJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa; // Para criar referência em toJpaEntity

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PontoFidelidadeMapper {

    private PontoFidelidadeMapper() {}

    public static PontoFidelidadeJpa toJpaEntity(PontoFidelidade domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        PontoFidelidadeJpa jpaEntity = new PontoFidelidadeJpa();

        if (domainEntity.getId() != null) {
            jpaEntity.setId(domainEntity.getId().getValorUUID());
        }

        // Mapeando ClienteId (VO do domínio) para ClienteJpa (referência na JPA)
        if (domainEntity.getClienteId() != null) {
            ClienteJpa clienteRef = new ClienteJpa(); // Cria uma instância "proxy" de ClienteJpa
            clienteRef.setId(domainEntity.getClienteId().getValor()); // Seta apenas o ID
            jpaEntity.setCliente(clienteRef); // Seta o objeto ClienteJpa na PontoFidelidadeJpa
        }

        jpaEntity.setQuantidadeOriginal(domainEntity.getQuantidadeOriginal());
        jpaEntity.setQuantidadeUtilizada(domainEntity.getQuantidadeUtilizada());
        jpaEntity.setDataAquisicao(domainEntity.getDataAquisicao());
        jpaEntity.setDataExpiracao(domainEntity.getDataExpiracao());

        if (domainEntity.getIngressoOrigemId() != null) {
            jpaEntity.setIngressoOrigemId(domainEntity.getIngressoOrigemId().getValor());
        }
        return jpaEntity;
    }

    public static PontoFidelidade toDomainEntity(PontoFidelidadeJpa jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        ClienteId clienteIdDominio = null;
        if (jpaEntity.getCliente() != null && jpaEntity.getCliente().getId() != null) {
            // Obtém o UUID do ClienteJpa e converte para ClienteId (VO do domínio)
            clienteIdDominio = ClienteId.de(jpaEntity.getCliente().getId());
        }

        IngressoId ingressoIdDominio = null;
        if (jpaEntity.getIngressoOrigemId() != null) {
            ingressoIdDominio = IngressoId.de(jpaEntity.getIngressoOrigemId());
        }

        return new PontoFidelidade(
                PontoFidelidadeId.de(jpaEntity.getId()),
                clienteIdDominio, // Passa o ClienteId (VO)
                jpaEntity.getQuantidadeOriginal(),
                jpaEntity.getQuantidadeUtilizada(),
                jpaEntity.getDataAquisicao(),
                jpaEntity.getDataExpiracao(),
                ingressoIdDominio
        );
    }

    public static List<PontoFidelidade> toDomainEntityList(List<PontoFidelidadeJpa> jpaEntityList) {
        if (jpaEntityList == null) {
            return Collections.emptyList();
        }
        return jpaEntityList.stream()
                .map(PontoFidelidadeMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    public static List<PontoFidelidadeJpa> toJpaEntityList(List<PontoFidelidade> domainEntityList) {
        if (domainEntityList == null) {
            return Collections.emptyList();
        }
        return domainEntityList.stream()
                .map(PontoFidelidadeMapper::toJpaEntity)
                .collect(Collectors.toList());
    }
}