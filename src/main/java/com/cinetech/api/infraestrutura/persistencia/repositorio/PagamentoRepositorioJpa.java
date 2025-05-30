package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.enums.StatusPagamento;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.pagamento.Pagamento;
import com.cinetech.api.dominio.modelos.pagamento.PagamentoId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.repositorios.PagamentoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.entidade.PagamentoJpa;
import com.cinetech.api.infraestrutura.persistencia.jpa.PagamentoJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.mapper.PagamentoMapper;
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
    private final PagamentoMapper pagamentoMapper;

    public PagamentoRepositorioJpa(PagamentoJpaRepository jpaRepositoryInternal, PagamentoMapper pagamentoMapper) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        this.pagamentoMapper = pagamentoMapper;
    }

    private Pagamento mapToDomain(PagamentoJpa jpaEntity) {
        if (jpaEntity == null) return null;
        return pagamentoMapper.toDomainEntity(jpaEntity);
    }

    private List<Pagamento> mapToDomainList(List<PagamentoJpa> jpaList) {
        if (jpaList == null) return Collections.emptyList();
        return jpaList.stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Pagamento salvar(Pagamento pagamentoDominio) {
        PagamentoJpa pagamentoJpa = pagamentoMapper.toJpaEntity(pagamentoDominio);
        PagamentoJpa salvoJpa = jpaRepositoryInternal.save(pagamentoJpa);
        return mapToDomain(salvoJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPorId(PagamentoId pagamentoIdDominio) {
        UUID idPrimitivo = pagamentoMapper.toPrimitiveId(pagamentoIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo).map(this::mapToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPorIngressoId(IngressoId ingressoIdDominio) {
        // O PagamentoMapper deve ter um método para converter IngressoId (VO) para UUID
        // ou podemos usar o getter do VO.
        UUID ingressoIdPrimitivo = ingressoIdDominio.getValor(); // Assumindo IngressoId.getValor() retorna UUID
        return jpaRepositoryInternal.findByIngressoId(ingressoIdPrimitivo).map(this::mapToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPorReservaEventoId(ReservaEventoId reservaEventoIdDominio) {
        UUID reservaEventoIdPrimitivo = reservaEventoIdDominio.getValor(); // Assumindo ReservaEventoId.getValor() retorna UUID
        return jpaRepositoryInternal.findByReservaEventoId(reservaEventoIdPrimitivo).map(this::mapToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPorIdTransacaoGateway(String idTransacaoGateway) {
        return jpaRepositoryInternal.findByIdTransacaoGateway(idTransacaoGateway).map(this::mapToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pagamento> buscarPorStatus(StatusPagamento status) {
        return mapToDomainList(jpaRepositoryInternal.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pagamento> buscarTodos() {
        return mapToDomainList(jpaRepositoryInternal.findAll());
    }
}
