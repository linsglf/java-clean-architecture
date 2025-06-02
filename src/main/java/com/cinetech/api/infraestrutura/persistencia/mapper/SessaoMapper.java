package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.dominio.modelos.filme.Filme; // Para o construtor de Sessao
import com.cinetech.api.dominio.modelos.sala.Sala;   // Para o construtor de Sessao
import com.cinetech.api.infraestrutura.persistencia.entidade.SessaoJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.FilmeJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.AssentoJpa; // Para a lista

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SessaoMapper {

    private SessaoMapper() {}

    public static UUID toPrimitiveId(SessaoId sessaoIdVo) {
        return sessaoIdVo == null ? null : sessaoIdVo.getValor();
    }

    public static SessaoId uuidToSessaoId(UUID uuid) {
        return uuid == null ? null : SessaoId.de(uuid);
    }

    public static SessaoJpa toJpaEntity(Sessao domainEntity) {
        if (domainEntity == null) return null;
        SessaoJpa jpaEntity = new SessaoJpa();
        if (domainEntity.getId() != null) {
            jpaEntity.setId(toPrimitiveId(domainEntity.getId()));
        }

        if (domainEntity.getFilme() != null) {
            // Assumindo que FilmeMapper.toJpaEntity cria um FilmeJpa "proxy" se necessário
            // ou um FilmeJpa completo. Se FilmeJpa só precisa do ID para FK:
            FilmeJpa filmeRef = new FilmeJpa();
            if (domainEntity.getFilme().getId() != null) {
                filmeRef.setId(FilmeMapper.toPrimitiveId(domainEntity.getFilme().getId()));
            }
            jpaEntity.setFilme(filmeRef);
        }

        if (domainEntity.getSala() != null) {
            SalaJpa salaRef = new SalaJpa();
            if (domainEntity.getSala().getId() != null) {
                salaRef.setId(SalaMapper.toPrimitiveId(domainEntity.getSala().getId()));
            }
            jpaEntity.setSala(salaRef);
        }

        jpaEntity.setDataHoraInicio(domainEntity.getDataHoraInicio());
        jpaEntity.setTipoExibicao(domainEntity.getTipoExibicao());
        jpaEntity.setPrecoIngressoBase(domainEntity.getPrecoIngressoBase());
        jpaEntity.setStatus(domainEntity.getStatus());

        if (domainEntity.getAssentos() != null) {
            List<AssentoJpa> assentosJpa = domainEntity.getAssentos().stream()
                    .map(assentoDominio -> {
                        AssentoJpa assentoJpa = AssentoMapper.toJpaEntity(assentoDominio);
                        if (assentoJpa != null) assentoJpa.setSessao(jpaEntity); // Mantém referência bidirecional
                        return assentoJpa;
                    })
                    .collect(Collectors.toList());
            jpaEntity.setAssentos(assentosJpa);
        } else {
            jpaEntity.setAssentos(new ArrayList<>());
        }
        return jpaEntity;
    }

    // Usado pelo SessaoRepositorioJpa para reconstruir o agregado
    public static Sessao toDomainEntity(SessaoJpa jpaEntity) {
        if (jpaEntity == null) return null;

        Filme filmeDominio = null;
        if (jpaEntity.getFilme() != null) {
            // Assumimos que FilmeMapper.toDomainEntity pode reconstruir Filme a partir de FilmeJpa
            filmeDominio = FilmeMapper.toDomainEntity(jpaEntity.getFilme());
        }

        Sala salaDominio = null;
        if (jpaEntity.getSala() != null) {
            salaDominio = SalaMapper.toDomainEntity(jpaEntity.getSala());
        }

        // Cria a Sessao de domínio (cabeçalho). A lista de assentos é inicializada vazia.
        Sessao sessaoDominio = new Sessao(
                uuidToSessaoId(jpaEntity.getId()),
                filmeDominio,
                salaDominio,
                jpaEntity.getDataHoraInicio(),
                jpaEntity.getTipoExibicao(),
                jpaEntity.getPrecoIngressoBase(),
                jpaEntity.getStatus()
                // O construtor de Sessao que usamos aqui inicializa 'assentos' como new ArrayList<>()
        );

        // Os Assentos de domínio são populados pelo SessaoRepositorioJpa
        // usando AssentoMapper.toDomainEntity(AssentoJpa, Sessao sessaoPaiDominio)
        // e depois sessaoDominio.adicionarAssento().
        // Se quiséssemos fazer aqui (menos ideal para a referência pai correta no Assento):
        /*
        if (jpaEntity.getAssentos() != null) {
            jpaEntity.getAssentos().forEach(assentoJpa -> {
                // Problema: como passar sessaoDominio para o construtor de Assento aqui?
                // AssentoMapper.toDomainEntity precisaria de @Context.
                // É melhor o repositório fazer isso.
                Assento assentoDominio = AssentoMapper.toDomainEntity(assentoJpa, sessaoDominio); // Chamada hipotética
                sessaoDominio.adicionarAssento(assentoDominio);
            });
        }
        */
        return sessaoDominio;
    }

    public static List<Sessao> toDomainEntityList(List<SessaoJpa> jpaEntityList) {
        if (jpaEntityList == null) return Collections.emptyList();
        return jpaEntityList.stream().map(SessaoMapper::toDomainEntity).collect(Collectors.toList());
    }
}