package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.enums.StatusReservaEvento;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEvento;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.repositorios.ReservaEventoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.entidade.ReservaEventoJpa;
import com.cinetech.api.infraestrutura.persistencia.jpa.ReservaEventoJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.mapper.ClienteMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.ReservaEventoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.SalaMapper;
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
    private final ReservaEventoMapper reservaEventoMapper;
    // Mappers auxiliares para converter IDs de VOs para primitivos, se necessário na lógica de consulta
    private final SalaMapper salaMapper;
    private final ClienteMapper clienteMapper;
    // private final PagamentoMapper pagamentoMapper; // Se ReservaEventoMapper precisar para PagamentoId

    public ReservaEventoRepositorioJpa(ReservaEventoJpaRepository jpaRepositoryInternal,
                                       ReservaEventoMapper reservaEventoMapper,
                                       SalaMapper salaMapper,
                                       ClienteMapper clienteMapper
            /*, PagamentoMapper pagamentoMapper */) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        this.reservaEventoMapper = reservaEventoMapper;
        this.salaMapper = salaMapper;
        this.clienteMapper = clienteMapper;
        // this.pagamentoMapper = pagamentoMapper;
    }

    // Método auxiliar para reconstruir o agregado (se ReservaEvento tiver coleções filhas complexas)
    // Para ReservaEvento, o mapeamento é mais direto se não houver filhos complexos no agregado.
    private ReservaEvento mapToDomain(ReservaEventoJpa jpaEntity) {
        if (jpaEntity == null) return null;
        // O ReservaEventoMapper deve ser configurado com 'uses' para os mappers dos IDs VO
        // ou ter métodos default para converter UUIDs para ClienteId, SalaId, PagamentoId.
        return reservaEventoMapper.toDomainEntity(jpaEntity);
    }

    private List<ReservaEvento> mapToDomainList(List<ReservaEventoJpa> jpaList) {
        if (jpaList == null) return Collections.emptyList();
        return jpaList.stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservaEvento salvar(ReservaEvento reservaEventoDominio) {
        ReservaEventoJpa reservaJpa = reservaEventoMapper.toJpaEntity(reservaEventoDominio);
        ReservaEventoJpa salvaJpa = jpaRepositoryInternal.save(reservaJpa);
        return mapToDomain(salvaJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservaEvento> buscarPorId(ReservaEventoId reservaEventoIdDominio) {
        UUID idPrimitivo = reservaEventoMapper.toPrimitiveId(reservaEventoIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo).map(this::mapToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarTodas() {
        return mapToDomainList(jpaRepositoryInternal.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarPorSalaId(SalaId salaIdDominio) {
        UUID salaIdPrimitivo = salaMapper.toPrimitiveId(salaIdDominio);
        return mapToDomainList(jpaRepositoryInternal.findBySala_Id(salaIdPrimitivo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarPorClienteId(ClienteId clienteIdDominio) {
        UUID clienteIdPrimitivo = clienteMapper.toPrimitiveId(clienteIdDominio);
        return mapToDomainList(jpaRepositoryInternal.findByCliente_Id(clienteIdPrimitivo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarReservasEventoConflitantesPorSalaEPeriodo(
            SalaId salaId, LocalDateTime inicioPeriodo, LocalDateTime fimPeriodo,
            Optional<ReservaEventoId> reservaEventoIdParaExcluirOptional) {

        UUID salaUUID = salaMapper.toPrimitiveId(salaId);
        UUID reservaExcluirUUID = reservaEventoIdParaExcluirOptional
                .map(reservaEventoMapper::toPrimitiveId).orElse(null);

        // Usa a query customizada do JpaRepository
        List<ReservaEventoJpa> conflitantesJpa = jpaRepositoryInternal.findReservasConflitantes(
                salaUUID, inicioPeriodo, fimPeriodo, reservaExcluirUUID
        );

        return mapToDomainList(conflitantesJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaEvento> buscarPorStatus(StatusReservaEvento status) {
        // Supondo que ReservaEventoJpaRepository tenha: List<ReservaEventoJpa> findByStatus(StatusReservaEvento status);
        // return mapToDomainList(jpaRepositoryInternal.findByStatus(status));
        // Implementação de exemplo se não houver:
        return jpaRepositoryInternal.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
}
