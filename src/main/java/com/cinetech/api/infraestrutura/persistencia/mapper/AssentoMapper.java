package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.assento.Assento;
import com.cinetech.api.dominio.modelos.assento.AssentoId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.sessao.Sessao; // Para o construtor de Assento (domínio)
import com.cinetech.api.infraestrutura.persistencia.entidade.AssentoJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.SessaoJpa; // Para criar referência em toJpaEntity

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class AssentoMapper {

    // Construtor privado para impedir instanciação
    private AssentoMapper() {}

    // --- Métodos Estáticos de Conversão de ID ---
    public static AssentoId uuidToAssentoId(UUID uuid) {
        return uuid == null ? null : AssentoId.de(uuid);
    }

    public static UUID assentoIdToUuid(AssentoId assentoId) {
        return assentoId == null ? null : assentoId.getValor();
    }

    // --- Mapeamento para Entidade JPA ---
    public static AssentoJpa toJpaEntity(Assento domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        AssentoJpa jpaEntity = new AssentoJpa();

        if (domainEntity.getId() != null) {
            jpaEntity.setId(assentoIdToUuid(domainEntity.getId()));
        }
        jpaEntity.setIdentificadorPosicao(domainEntity.getIdentificadorPosicao());
        jpaEntity.setTipo(domainEntity.getTipo());
        jpaEntity.setStatus(domainEntity.getStatus());

        if (domainEntity.getClienteIdReservaTemporaria() != null) {
            // Assume que AssentoJpa.clienteIdReservaTemporaria é UUID
            jpaEntity.setClienteIdReservaTemporaria(domainEntity.getClienteIdReservaTemporaria().getValor());
        } else {
            jpaEntity.setClienteIdReservaTemporaria(null);
        }
        jpaEntity.setTimestampExpiracaoReserva(domainEntity.getTimestampExpiracaoReserva());

        // Seta a referência para SessaoJpa (apenas com ID)
        if (domainEntity.getSessao() != null && domainEntity.getSessao().getId() != null) {
            SessaoJpa sessaoRef = new SessaoJpa();
            sessaoRef.setId(domainEntity.getSessao().getId().getValor()); // Pega o UUID do SessaoId
            jpaEntity.setSessao(sessaoRef); // Assumindo que AssentoJpa tem setSessao(SessaoJpa)
        }
        return jpaEntity;
    }

    // --- Mapeamento para Entidade de Domínio ---
    // Este método é usado pelo SessaoRepositorioJpa para construir o Assento de domínio
    // dentro do contexto de uma Sessao de domínio já existente.
    public static Assento toDomainEntity(AssentoJpa jpaEntity, Sessao sessaoDominioPai) {
        if (jpaEntity == null) {
            return null;
        }
        Objects.requireNonNull(sessaoDominioPai, "Sessao de domínio pai não pode ser nula para mapear Assento.");

        ClienteId clienteIdReserva = null;
        if (jpaEntity.getClienteIdReservaTemporaria() != null) {
            clienteIdReserva = ClienteId.de(jpaEntity.getClienteIdReservaTemporaria());
        }

        // Usa o construtor completo de Assento (domínio)
        return new Assento(
                uuidToAssentoId(jpaEntity.getId()),
                sessaoDominioPai, // Injeta a referência da Sessao de domínio pai
                jpaEntity.getIdentificadorPosicao(),
                jpaEntity.getTipo(),
                jpaEntity.getStatus(),
                clienteIdReserva,
                jpaEntity.getTimestampExpiracaoReserva()
        );
    }

    public static List<AssentoJpa> toJpaEntityList(List<Assento> domainEntityList) {
        if (domainEntityList == null) {
            return Collections.emptyList();
        }
        return domainEntityList.stream()
                .map(AssentoMapper::toJpaEntity) // Chama o toJpaEntity estático
                .collect(Collectors.toList());
    }

    // Este método de lista para toDomainEntity precisaria da Sessao pai para cada Assento.
    // Geralmente, a lista de Assentos de domínio é construída iterativamente no SessaoRepositorioJpa,
    // onde a Sessao pai já está disponível.
    public static List<Assento> toDomainEntityList(List<AssentoJpa> jpaEntityList, Sessao sessaoDominioPai) {
        if (jpaEntityList == null) {
            return Collections.emptyList();
        }
        return jpaEntityList.stream()
                .map(jpaEntity -> toDomainEntity(jpaEntity, sessaoDominioPai))
                .collect(Collectors.toList());
    }
}