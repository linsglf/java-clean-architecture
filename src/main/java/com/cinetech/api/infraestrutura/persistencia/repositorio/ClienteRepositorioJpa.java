package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.repositorios.ClienteRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.ClienteJpaRepository;
import com.cinetech.api.infraestrutura.persistencia.mapper.ClienteMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.CreditoCompensacaoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.PontoFidelidadeMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ClienteRepositorioJpa implements ClienteRepositorio {

    private final ClienteJpaRepository jpaRepositoryInternal;
    private final ClienteMapper clienteMapper;
    // Mappers para coleções agregadas, se o ClienteMapper precisar deles explicitamente
    // ou se a reconstrução do agregado for feita aqui.
    private final CreditoCompensacaoMapper creditoCompensacaoMapper;
    private final PontoFidelidadeMapper pontoFidelidadeMapper;


    public ClienteRepositorioJpa(ClienteJpaRepository jpaRepositoryInternal, ClienteMapper clienteMapper,
                                 CreditoCompensacaoMapper creditoCompensacaoMapper, PontoFidelidadeMapper pontoFidelidadeMapper) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
        this.clienteMapper = clienteMapper;
        this.creditoCompensacaoMapper = creditoCompensacaoMapper;
        this.pontoFidelidadeMapper = pontoFidelidadeMapper;
    }

    // Método auxiliar para reconstruir o agregado Cliente com suas listas
    private Cliente reconstruirAgregadoCliente(com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa clienteJpa) {
        if (clienteJpa == null) return null;
        // O ClienteMapper deve ser configurado com `uses = {CreditoCompensacaoMapper.class, PontoFidelidadeMapper.class}`
        // para mapear as coleções creditosCompensacaoJpa e pontosFidelidadeJpa.
        return clienteMapper.toDomainEntity(clienteJpa);
    }

    private List<Cliente> reconstruirListaAgregadosCliente(List<com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa> clientesJpa) {
        return clientesJpa.stream().map(this::reconstruirAgregadoCliente).collect(Collectors.toList());
    }


    @Override
    @Transactional // Modifica o estado, incluindo coleções filhas
    public Cliente salvar(Cliente clienteDominio) {
        com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa clienteJpa = clienteMapper.toJpaEntity(clienteDominio);
        // Garante o relacionamento bidirecional para JPA antes de salvar
        if (clienteJpa.getCreditosCompensacaoJpa() != null) {
            clienteJpa.getCreditosCompensacaoJpa().forEach(credito -> credito.setCliente(clienteJpa));
        }
        if (clienteJpa.getPontosFidelidadeJpa() != null) {
            clienteJpa.getPontosFidelidadeJpa().forEach(ponto -> ponto.setCliente(clienteJpa));
        }
        com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa salvoJpa = jpaRepositoryInternal.save(clienteJpa);
        return reconstruirAgregadoCliente(salvoJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(ClienteId clienteIdDominio) {
        UUID idPrimitivo = clienteMapper.toPrimitiveId(clienteIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(this::reconstruirAgregadoCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return jpaRepositoryInternal.findByCpf(cpf)
                .map(this::reconstruirAgregadoCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        return jpaRepositoryInternal.findByEmail(email)
                .map(this::reconstruirAgregadoCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarTodos() {
        return reconstruirListaAgregadosCliente(jpaRepositoryInternal.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorIdValorPrimitivo(UUID idPrimitivo) {
        return jpaRepositoryInternal.existsById(idPrimitivo);
    }
}
