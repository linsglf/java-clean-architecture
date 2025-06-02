package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.enums.StatusReservaEvento;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEvento;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.repositorios.ReservaEventoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.entidade.ReservaEventoJpa;
import com.cinetech.api.infraestrutura.persistencia.jpa.ReservaEventoJpaRepository;
// Importe as CLASSES dos mappers para chamadas estáticas
import com.cinetech.api.infraestrutura.persistencia.mapper.ClienteMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.ReservaEventoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.SalaMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.PagamentoMapper; // Para converter PagamentoId

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ReservaEventoRepositorioJpa implements ReservaEventoRepositorio {

    private final ReservaEventoJpaRepository jpaRepositoryInternal;
    // Mappers não são injetados

    public ReservaEventoRepositorioJpa(ReservaEventoJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
    }

    @Override
    @Transactional
    public ReservaEvento salvar(ReservaEvento reservaEventoDominio) {
        ReservaEventoJpa reservaJpa = ReservaEventoMapper.toJpaEntity(reservaEventoDominio); // Chamada estática
        ReservaEventoJpa salvaJpa = jpaRepositoryInternal.save(reservaJpa);
        return ReservaEventoMapper.toDomainEntity(salvaJpa); // Chamada estática
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservaEvento> buscarPorId(ReservaEventoId reservaEventoIdDominio) {
        UUID idPrimitivo = ReservaEventoMapper.toPrimitiveId(reservaEventoIdDominio); // Chamada estática
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(ReservaEventoMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarTodas() {
        return jpaRepositoryInternal.findAll().stream()
                .map(ReservaEventoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarPorSalaId(SalaId salaIdDominio) {
        UUID salaIdPrimitivo = SalaMapper.toPrimitiveId(salaIdDominio); // Chamada estática
        return jpaRepositoryInternal.findBySala_Id(salaIdPrimitivo).stream()
                .map(ReservaEventoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarPorClienteId(ClienteId clienteIdDominio) {
        UUID clienteIdPrimitivo = ClienteMapper.toPrimitiveId(clienteIdDominio); // Chamada estática
        return jpaRepositoryInternal.findByCliente_Id(clienteIdPrimitivo).stream()
                .map(ReservaEventoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarReservasEventoConflitantesPorSalaEPeriodo(
            SalaId salaId, LocalDateTime inicioPeriodo, LocalDateTime fimPeriodo,
            Optional<ReservaEventoId> reservaEventoIdParaExcluirOptional) {

        UUID salaUUID = SalaMapper.toPrimitiveId(salaId); // Chamada estática
        UUID reservaExcluirUUID = reservaEventoIdParaExcluirOptional
                .map(ReservaEventoMapper::toPrimitiveId).orElse(null); // Chamada estática

        List<ReservaEventoJpa> conflitantesJpa = jpaRepositoryInternal.findReservasConflitantes(
                salaUUID, inicioPeriodo, fimPeriodo, reservaExcluirUUID
        );
        return conflitantesJpa.stream()
                .map(ReservaEventoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarPorStatus(StatusReservaEvento status) {
        return jpaRepositoryInternal.findByStatus(status).stream() // Assume que findByStatus existe em ReservaEventoJpaRepository
                .map(ReservaEventoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}