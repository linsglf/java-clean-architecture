package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.sala.Sala;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.repositorios.SalaRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.SalaJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.mapper.SalaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class SalaRepositorioJpa implements SalaRepositorio {

    private final SalaJpaRepository jpaRepositoryInternal;
    private final SalaMapper salaMapper;

    public SalaRepositorioJpa(SalaJpaRepository jpaRepositoryInternal, SalaMapper salaMapper) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        this.salaMapper = salaMapper;
    }

    @Override
    public Sala salvar(Sala salaDominio) {
        com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa salaJpa = salaMapper.toJpaEntity(salaDominio);
        com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa salvaJpa = jpaRepositoryInternal.save(salaJpa);
        return salaMapper.toDomainEntity(salvaJpa);
    }

    @Override
    public Optional<Sala> buscarPorId(SalaId salaIdDominio) {
        UUID idPrimitivo = salaMapper.toPrimitiveId(salaIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(salaMapper::toDomainEntity);
    }

    @Override
    public Optional<Sala> buscarPorNome(String nome) {
        return jpaRepositoryInternal.findByNome(nome)
                .map(salaMapper::toDomainEntity);
    }

    @Override
    public List<Sala> buscarTodas() {
        return jpaRepositoryInternal.findAll().stream()
                .map(salaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sala> buscarSalasDisponiveisParaEventos() {
        return jpaRepositoryInternal.findByDisponivelParaEventosTrue().stream()
                .map(salaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}
