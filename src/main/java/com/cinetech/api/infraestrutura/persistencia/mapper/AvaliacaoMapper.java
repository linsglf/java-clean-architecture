package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.avaliacao.Avaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.AvaliacaoId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.infraestrutura.persistencia.entidade.AvaliacaoJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AvaliacaoMapper {

    @Named("avaliacaoIdToUuid")
    default UUID avaliacaoIdToUuid(AvaliacaoId avaliacaoId) {
        return avaliacaoId == null ? null : avaliacaoId.getValor();
    }

    @Named("uuidToAvaliacaoId")
    default AvaliacaoId uuidToAvaliacaoId(UUID uuid) {
        return uuid == null ? null : AvaliacaoId.de(uuid.toString());
    }

    default UUID toPrimitiveId(AvaliacaoId avaliacaoIdVo) {
        return avaliacaoIdVo == null ? null : avaliacaoIdVo.getValor();
    }

    // Mapeadores para IDs
    default UUID filmeIdToUuid(FilmeId filmeId) { return filmeId == null ? null : filmeId.getValor(); }
    default FilmeId uuidToFilmeId(UUID uuid) { return uuid == null ? null : FilmeId.de(uuid.toString()); }
    default UUID clienteIdToUuid(ClienteId clienteId) { return clienteId == null ? null : clienteId.getValor(); }
    default ClienteId uuidToClienteId(UUID uuid) { return uuid == null ? null : ClienteId.de(uuid.toString()); }


    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToAvaliacaoId")
    @Mapping(source = "filmeId", target = "filmeId")
    @Mapping(source = "clienteId", target = "clienteId")
    Avaliacao toDomainEntity(AvaliacaoJpa jpaEntity);

    @Mapping(source = "id", target = "id", qualifiedByName = "avaliacaoIdToUuid")
    @Mapping(source = "filmeId", target = "filmeId")
    @Mapping(source = "clienteId", target = "clienteId")
    AvaliacaoJpa toJpaEntity(Avaliacao domainEntity);

    List<Avaliacao> toDomainEntityList(List<AvaliacaoJpa> jpaEntityList);
}
