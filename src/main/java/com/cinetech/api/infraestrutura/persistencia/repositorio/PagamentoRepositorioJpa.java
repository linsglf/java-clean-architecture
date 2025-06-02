package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.pagamento.Pagamento;
import com.cinetech.api.dominio.modelos.pagamento.PagamentoId;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.enums.StatusPagamento;
import com.cinetech.api.dominio.repositorios.PagamentoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.PagamentoJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.entidade.PagamentoJpa;
// Importe as CLASSES dos mappers para chamadas estáticas
import com.cinetech.api.infraestrutura.persistencia.mapper.PagamentoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.IngressoMapper; // Para toPrimitiveId de IngressoId
import com.cinetech.api.infraestrutura.persistencia.mapper.ReservaEventoMapper; // Para toPrimitiveId de ReservaEventoId

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PagamentoRepositorioJpa implements PagamentoRepositorio {

    private final PagamentoJpaRepository jpaRepositoryInternal;
    // Mappers não são injetados

    public PagamentoRepositorioJpa(PagamentoJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
    }

    @Override
    @Transactional
    public Pagamento salvar(Pagamento pagamentoDominio) {
        PagamentoJpa pagamentoJpa = PagamentoMapper.toJpaEntity(pagamentoDominio); // Chamada estática
        PagamentoJpa salvoJpa = jpaRepositoryInternal.save(pagamentoJpa);
        return PagamentoMapper.toDomainEntity(salvoJpa); // Chamada estática
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPorId(PagamentoId pagamentoIdDominio) {
        UUID idPrimitivo = PagamentoMapper.toPrimitiveId(pagamentoIdDominio); // Chamada estática
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(PagamentoMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPorIngressoId(IngressoId ingressoIdDominio) {
        UUID ingressoIdPrimitivo = IngressoMapper.toPrimitiveId(ingressoIdDominio); // Chamada estática
        return jpaRepositoryInternal.findByIngressoId(ingressoIdPrimitivo)
                .map(PagamentoMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPorReservaEventoId(ReservaEventoId reservaEventoIdDominio) {
        UUID reservaEventoIdPrimitivo = ReservaEventoMapper.toPrimitiveId(reservaEventoIdDominio); // Chamada estática
        return jpaRepositoryInternal.findByReservaEventoId(reservaEventoIdPrimitivo)
                .map(PagamentoMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPorIdTransacaoGateway(String idTransacaoGateway) {
        return jpaRepositoryInternal.findByIdTransacaoGateway(idTransacaoGateway)
                .map(PagamentoMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pagamento> buscarPorStatus(StatusPagamento status) {
        return jpaRepositoryInternal.findByStatus(status).stream()
                .map(PagamentoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pagamento> buscarTodos() {
        return jpaRepositoryInternal.findAll().stream()
                .map(PagamentoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}