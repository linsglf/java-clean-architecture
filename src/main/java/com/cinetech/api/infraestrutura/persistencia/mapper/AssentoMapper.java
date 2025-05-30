package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.assento.Assento;
import com.cinetech.api.dominio.modelos.assento.AssentoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.AssentoJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime; // Para o timestampExpiracaoReserva

@Mapper(componentModel = "spring", uses = {ClienteMapper.class}) // ClienteMapper para clienteIdReservaTemporaria
public interface AssentoMapper {

    @Named("assentoIdToUuid")
    default UUID assentoIdToUuid(AssentoId assentoId) {
        return assentoId == null ? null : assentoId.getValor();
    }

    @Named("uuidToAssentoId")
    default AssentoId uuidToAssentoId(UUID uuid) {
        return uuid == null ? null : AssentoId.de(uuid.toString());
    }

    default UUID toPrimitiveId(AssentoId assentoIdVo) {
        return assentoIdVo == null ? null : assentoIdVo.getValor();
    }

    // Este método será usado pelo SessaoRepositorioJpa para construir o Assento de domínio
    // manualmente, pois o construtor do Assento de domínio precisa da Sessao de domínio.
    // O MapStruct teria dificuldade em injetar a Sessao de domínio correta automaticamente aqui.
    // Portanto, este mapper pode ser mais simples e focado nos tipos de ID e primitivos,
    // e o repositório faz a construção do objeto de domínio Assento.

    // Vamos focar no mapeamento para JpaEntity, onde é mais direto
    @Mapping(source = "id", target = "id", qualifiedByName = "assentoIdToUuid")
    @Mapping(source = "sessao.id", target = "sessao.id") // Mapeia o ID da SessaoJpa
    @Mapping(source = "clienteIdReservaTemporaria.valor", target = "clienteIdReservaTemporaria") // Mapeia ClienteId (VO) para UUID
    @Mapping(source = "identificadorPosicao", target = "identificadorPosicao")
    @Mapping(source = "tipo", target = "tipo")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "timestampExpiracaoReserva", target = "timestampExpiracaoReserva")
    AssentoJpa toJpaEntity(Assento domainEntity);

    // Helper para o mapeamento de ClienteId (VO) para UUID (se não estiver no ClienteMapper ou para uso direto)
    // Se ClienteMapper for usado em 'uses', MapStruct pode lidar com isso.
    // default UUID clienteIdToUuid(ClienteId clienteId) {
    //     return clienteId == null ? null : clienteId.getValor();
    // }
    // default ClienteId uuidToClienteId(UUID uuid) {
    //     return uuid == null ? null : ClienteId.de(uuid.toString());
    // }
}
