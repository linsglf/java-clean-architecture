package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.ingresso.Ingresso;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.dominio.repositorios.IngressoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.IngressoJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.mapper.AssentoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.ClienteMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.IngressoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.SessaoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class IngressoRepositorioJpa implements IngressoRepositorio {

    private final IngressoJpaRepository jpaRepositoryInternal;
    private final IngressoMapper ingressoMapper;
    // Mappers para reconstruir o Ingresso de domínio com suas dependências
    private final ClienteMapper clienteMapper;
    private final SessaoMapper sessaoMapper;
    private final AssentoMapper assentoMapper;
    // PromocaoMapper é usado pelo IngressoMapper se promocaoAplicadaId for mapeado para PromocaoId (VO)

    public IngressoRepositorioJpa(IngressoJpaRepository jpaRepositoryInternal, IngressoMapper ingressoMapper,
                                  ClienteMapper clienteMapper, SessaoMapper sessaoMapper, AssentoMapper assentoMapper) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        this.ingressoMapper = ingressoMapper;
        this.clienteMapper = clienteMapper;
        this.sessaoMapper = sessaoMapper;
        this.assentoMapper = assentoMapper;
    }

    // Método auxiliar para reconstruir o Ingresso de domínio
    // O IngressoMapper deve ser configurado com 'uses' para os mappers das entidades relacionadas
    private Ingresso reconstruirAgregadoIngresso(com.cinetech.api.infraestrutura.persistencia.entidade.IngressoJpa ingressoJpa) {
        if (ingressoJpa == null) return null;
        return ingressoMapper.toDomainEntity(ingressoJpa);
    }

    private List<Ingresso> reconstruirListaAgregadosIngresso(List<com.cinetech.api.infraestrutura.persistencia.entidade.IngressoJpa> ingressosJpa) {
        return ingressosJpa.stream().map(this::reconstruirAgregadoIngresso).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Ingresso salvar(Ingresso ingressoDominio) {
        com.cinetech.api.infraestrutura.persistencia.entidade.IngressoJpa ingressoJpa = ingressoMapper.toJpaEntity(ingressoDominio);
        com.cinetech.api.infraestrutura.persistencia.entidade.IngressoJpa salvoJpa = jpaRepositoryInternal.save(ingressoJpa);
        return reconstruirAgregadoIngresso(salvoJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingresso> buscarPorId(IngressoId ingressoIdDominio) {
        UUID idPrimitivo = ingressoMapper.toPrimitiveId(ingressoIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(this::reconstruirAgregadoIngresso);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingresso> buscarPorCodigoValidacao(String codigoValidacao) {
        return jpaRepositoryInternal.findByCodigoValidacao(codigoValidacao)
                .map(this::reconstruirAgregadoIngresso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresso> buscarPorSessaoId(SessaoId sessaoIdDominio) {
        UUID sessaoIdPrimitivo = sessaoMapper.toPrimitiveId(sessaoIdDominio);
        return reconstruirListaAgregadosIngresso(jpaRepositoryInternal.findBySessao_Id(sessaoIdPrimitivo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresso> buscarPorClienteId(ClienteId clienteIdDominio) {
        UUID clienteIdPrimitivo = clienteMapper.toPrimitiveId(clienteIdDominio);
        return reconstruirListaAgregadosIngresso(jpaRepositoryInternal.findByCliente_Id(clienteIdPrimitivo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresso> buscarTodos() {
        return reconstruirListaAgregadosIngresso(jpaRepositoryInternal.findAll());
    }
}
