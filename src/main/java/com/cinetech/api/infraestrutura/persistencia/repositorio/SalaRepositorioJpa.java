package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.sala.Sala;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.repositorios.SalaRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.SalaJpaRepository; // Interface Spring Data JPA
import com.cinetech.api.infraestrutura.persistencia.mapper.SalaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class SalaRepositorioJpa implements SalaRepositorio {

    private final SalaJpaRepository jpaRepositoryInternal;
    // private final SalaMapper salaMapper; // <<< REMOVA ESTE CAMPO E A INJEÇÃO DELE

    // Construtor ajustado: não injeta mais SalaMapper
    public SalaRepositorioJpa(SalaJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        // this.salaMapper = salaMapper; // <<< REMOVA ESTA LINHA
    }

    @Override
    public Sala salvar(Sala salaDominio) {
        // Chama o método estático do SalaMapper
        com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa salaJpa = SalaMapper.toJpaEntity(salaDominio);
        com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa salvaJpa = jpaRepositoryInternal.save(salaJpa);
        // Chama o método estático do SalaMapper
        return SalaMapper.toDomainEntity(salvaJpa);
    }

    @Override
    public Optional<Sala> buscarPorId(SalaId salaIdDominio) {
        // Chama o método estático do SalaMapper
        UUID idPrimitivo = SalaMapper.toPrimitiveId(salaIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo)
                // Usa referência de método estático
                .map(SalaMapper::toDomainEntity);
    }

    @Override
    public Optional<Sala> buscarPorNome(String nome) {
        return jpaRepositoryInternal.findByNome(nome)
                // Usa referência de método estático
                .map(SalaMapper::toDomainEntity);
    }

    @Override
    public List<Sala> buscarTodas() {
        return jpaRepositoryInternal.findAll().stream()
                // Usa referência de método estático
                .map(SalaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sala> buscarSalasDisponiveisParaEventos() {
        return jpaRepositoryInternal.findByDisponivelParaEventosTrue().stream()
                // Usa referência de método estático
                .map(SalaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}