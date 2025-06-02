package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.enums.TipoPromocao;
import com.cinetech.api.dominio.modelos.promocao.Promocao;
import com.cinetech.api.dominio.modelos.promocao.PromocaoId;
import com.cinetech.api.dominio.repositorios.PromocaoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.entidade.PromocaoJpa;
import com.cinetech.api.infraestrutura.persistencia.jpa.PromocaoJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.mapper.PromocaoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PromocaoRepositorioJpa implements PromocaoRepositorio {

    private final PromocaoJpaRepository jpaRepositoryInternal;

    public PromocaoRepositorioJpa(PromocaoJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
    }

    private Promocao mapToDomain(PromocaoJpa jpaEntity) {
        if (jpaEntity == null) return null;
        return PromocaoMapper.toDomainEntity(jpaEntity);
    }

    private List<Promocao> mapToDomainList(List<PromocaoJpa> jpaList) {
        if (jpaList == null) return Collections.emptyList();
        return jpaList.stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Promocao salvar(Promocao promocaoDominio) {
        PromocaoJpa promocaoJpa = PromocaoMapper.toJpaEntity(promocaoDominio);
        PromocaoJpa salvaJpa = jpaRepositoryInternal.save(promocaoJpa);
        return mapToDomain(salvaJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Promocao> buscarPorId(PromocaoId promocaoIdDominio) {
        UUID idPrimitivo = PromocaoMapper.toPrimitiveId(promocaoIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo).map(this::mapToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Promocao> buscarPorNomeDescritivo(String nomeDescritivo) {
        return jpaRepositoryInternal.findByNomeDescritivo(nomeDescritivo).map(this::mapToDomain);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Promocao> buscarTodas() {
        return mapToDomainList(jpaRepositoryInternal.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promocao> buscarPromocoesVigentes(LocalDate dataReferencia) {
        return mapToDomainList(jpaRepositoryInternal.findPromocoesVigentes(dataReferencia));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promocao> buscarPorTipo(TipoPromocao tipoPromocao) {
        return mapToDomainList(jpaRepositoryInternal.findByTipoPromocao(tipoPromocao));
    }
}
