package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.ingresso.Ingresso;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.repositorios.IngressoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.IngressoJpaRepository; // Interface Spring Data JPA
import com.cinetech.api.infraestrutura.persistencia.entidade.IngressoJpa; // Entidade JPA
// Importe as CLASSES dos mappers para chamadas estáticas
import com.cinetech.api.infraestrutura.persistencia.mapper.IngressoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.SessaoMapper;  // Para converter SessaoId
import com.cinetech.api.infraestrutura.persistencia.mapper.ClienteMapper; // Para converter ClienteId

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class IngressoRepositorioJpa implements IngressoRepositorio {

    private final IngressoJpaRepository jpaRepositoryInternal;
    // Mappers não são mais injetados

    public IngressoRepositorioJpa(IngressoJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
    }

    @Override
    @Transactional
    public Ingresso salvar(Ingresso ingressoDominio) {
        IngressoJpa ingressoJpa = IngressoMapper.toJpaEntity(ingressoDominio); // Chamada estática
        // Lógica para setar referências de objetos JPA em ingressoJpa (ex: cliente, sessao, assento)
        // já deve estar dentro de IngressoMapper.toJpaEntity se ele cria os proxies
        IngressoJpa salvoJpa = jpaRepositoryInternal.save(ingressoJpa);
        return IngressoMapper.toDomainEntity(salvoJpa); // Chamada estática
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingresso> buscarPorId(IngressoId ingressoIdDominio) {
        UUID idPrimitivo = IngressoMapper.toPrimitiveId(ingressoIdDominio); // Chamada estática
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(IngressoMapper::toDomainEntity); // Referência a método estático
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingresso> buscarPorCodigoValidacao(String codigoValidacao) {
        return jpaRepositoryInternal.findByCodigoValidacao(codigoValidacao)
                .map(IngressoMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresso> buscarPorSessaoId(SessaoId sessaoIdDominio) {
        UUID sessaoIdPrimitivo = SessaoMapper.toPrimitiveId(sessaoIdDominio); // Chamada estática a SessaoMapper
        return jpaRepositoryInternal.findBySessao_Id(sessaoIdPrimitivo).stream()
                .map(IngressoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresso> buscarPorClienteId(ClienteId clienteIdDominio) {
        UUID clienteIdPrimitivo = ClienteMapper.toPrimitiveId(clienteIdDominio); // Chamada estática a ClienteMapper
        return jpaRepositoryInternal.findByCliente_Id(clienteIdPrimitivo).stream()
                .map(IngressoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresso> buscarTodos() {
        return jpaRepositoryInternal.findAll().stream()
                .map(IngressoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}