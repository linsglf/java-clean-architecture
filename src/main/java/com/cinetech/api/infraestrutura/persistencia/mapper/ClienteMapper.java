package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa;
import com.cinetech.api.dominio.modelos.credito.CreditoCompensacao;
import com.cinetech.api.infraestrutura.persistencia.entidade.CreditoCompensacaoJpa;
import com.cinetech.api.dominio.modelos.pontofidelidade.PontoFidelidade;
import com.cinetech.api.infraestrutura.persistencia.entidade.PontoFidelidadeJpa;


import java.util.Collections;
import java.util.List;
import java.util.UUID; // Importar UUID
import java.util.stream.Collectors;
import java.util.ArrayList;

public class ClienteMapper {

    private ClienteMapper() {}

    // <<< MÉTODO ADICIONADO/GARANTIDO >>>
    public static UUID toPrimitiveId(ClienteId clienteIdVo) {
        return clienteIdVo == null ? null : clienteIdVo.getValor();
    }

    // --- MÉTODOS DE CONVERSÃO DE ID ADICIONADOS ---
    public static ClienteId uuidToClienteId(UUID uuid) {
        return uuid == null ? null : ClienteId.de(uuid);
    }

    public static UUID clienteIdToUuid(ClienteId clienteId) {
        return clienteId == null ? null : clienteId.getValor();
    }
    // --- FIM DOS MÉTODOS DE CONVERSÃO DE ID ---

    public static ClienteJpa toJpaEntity(Cliente domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        ClienteJpa jpaEntity = new ClienteJpa();
        if (domainEntity.getId() != null) {
            // Usa o método de conversão local
            jpaEntity.setId(clienteIdToUuid(domainEntity.getId()));
        }
        jpaEntity.setNome(domainEntity.getNome());
        jpaEntity.setEmail(domainEntity.getEmail());
        jpaEntity.setCpf(domainEntity.getCpf());
        jpaEntity.setPerfil(domainEntity.getPerfil());

        if (domainEntity.getCreditosCompensacao() != null) {
            List<CreditoCompensacaoJpa> creditosJpa = domainEntity.getCreditosCompensacao().stream()
                    .map(creditoDominio -> {
                        // Assume que CreditoCompensacaoMapper.toJpaEntity espera CreditoCompensacao
                        CreditoCompensacaoJpa credJpa = CreditoCompensacaoMapper.toJpaEntity(creditoDominio);
                        if (credJpa != null) credJpa.setCliente(jpaEntity);
                        return credJpa;
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            jpaEntity.setCreditosCompensacaoJpa(creditosJpa);
        } else {
            jpaEntity.setCreditosCompensacaoJpa(new ArrayList<>());
        }

        if (domainEntity.getPontosFidelidade() != null) {
            List<PontoFidelidadeJpa> pontosJpa = domainEntity.getPontosFidelidade().stream()
                    .map(pontoDominio -> {
                        // Assume que PontoFidelidadeMapper.toJpaEntity espera PontoFidelidade
                        PontoFidelidadeJpa pontoJpa = PontoFidelidadeMapper.toJpaEntity(pontoDominio);
                        if (pontoJpa != null) pontoJpa.setCliente(jpaEntity);
                        return pontoJpa;
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            jpaEntity.setPontosFidelidadeJpa(pontosJpa);
        } else {
            jpaEntity.setPontosFidelidadeJpa(new ArrayList<>());
        }

        return jpaEntity;
    }

    public static Cliente toDomainEntity(ClienteJpa jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        List<CreditoCompensacao> creditosDominio = Collections.emptyList();
        if (jpaEntity.getCreditosCompensacaoJpa() != null) {
            creditosDominio = jpaEntity.getCreditosCompensacaoJpa().stream()
                    .map(creditoJpa -> CreditoCompensacaoMapper.toDomainEntity(creditoJpa)) // Passa ClienteJpaPai se CreditoCompensacaoMapper precisar
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        }

        List<PontoFidelidade> pontosDominio = Collections.emptyList();
        if (jpaEntity.getPontosFidelidadeJpa() != null) {
            pontosDominio = jpaEntity.getPontosFidelidadeJpa().stream()
                    .map(pontoJpa -> PontoFidelidadeMapper.toDomainEntity(pontoJpa)) // Passa ClienteJpaPai se PontoFidelidadeMapper precisar
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        }

        Cliente clienteDominio = new Cliente(
                // Usa o método de conversão local
                uuidToClienteId(jpaEntity.getId()),
                jpaEntity.getNome(),
                jpaEntity.getEmail(),
                jpaEntity.getCpf(),
                jpaEntity.getPerfil(),
                creditosDominio,
                pontosDominio
        );

        // Se CreditoCompensacao e PontoFidelidade de domínio precisarem da referência ao Cliente pai
        // no construtor, o mapeamento das listas acima precisaria passar 'clienteDominio' para
        // os métodos toDomainEntity dos mappers filhos, ou os filhos seriam construídos
        // e adicionados ao clienteDominio após a sua criação.
        // No nosso modelo atual, CreditoCompensacao e PontoFidelidade já recebem ClienteId no construtor.

        return clienteDominio;
    }

    public static List<Cliente> toDomainEntityList(List<ClienteJpa> jpaEntityList) {
        if (jpaEntityList == null) {
            return Collections.emptyList();
        }
        return jpaEntityList.stream()
                .map(ClienteMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    public static List<ClienteJpa> toJpaEntityList(List<Cliente> domainEntityList) {
        if (domainEntityList == null) {
            return Collections.emptyList();
        }
        return domainEntityList.stream()
                .map(ClienteMapper::toJpaEntity)
                .collect(Collectors.toList());
    }
}