package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.infraestrutura.persistencia.entidade.SessaoJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {FilmeMapper.class, SalaMapper.class, AssentoMapper.class})
public interface SessaoMapper {

    @Named("sessaoIdToUuid")
    default UUID sessaoIdToUuid(SessaoId sessaoId) {
        return sessaoId == null ? null : sessaoId.getValor();
    }

    @Named("uuidToSessaoId")
    default SessaoId uuidToSessaoId(UUID uuid) {
        return uuid == null ? null : SessaoId.de(uuid.toString());
    }

    default UUID toPrimitiveId(SessaoId sessaoIdVo) {
        return sessaoIdVo == null ? null : sessaoIdVo.getValor();
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToSessaoId")
    @Mapping(source = "filme", target = "filme") // Usa FilmeMapper
    @Mapping(source = "sala", target = "sala")   // Usa SalaMapper
    @Mapping(target = "assentos", ignore = true)  // IGNORA a lista de assentos aqui
    Sessao toDomainEntity(SessaoJpa jpaEntity); // Renomeado de toDomainEntityHeaderOnly

    @Mapping(source = "id", target = "id", qualifiedByName = "sessaoIdToUuid")
    @Mapping(source = "filme", target = "filme")
    @Mapping(source = "sala", target = "sala")
    @Mapping(source = "assentos", target = "assentos") // Usa AssentoMapper para converter List<Assento> para List<AssentoJpa>
    SessaoJpa toJpaEntity(Sessao domainEntity);

    // Este método de lista usará o toDomainEntity acima, que ignora os assentos.
    // A população dos assentos na lista de Sessões de domínio será feita no repositório.
    List<Sessao> toDomainEntityList(List<SessaoJpa> jpaEntityList);
}
