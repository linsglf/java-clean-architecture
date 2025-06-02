package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.repositorios.ClienteRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.ClienteJpaRepository;
// Importe as CLASSES dos mappers para chamadas estáticas
import com.cinetech.api.infraestrutura.persistencia.mapper.ClienteMapper;
// CreditoCompensacaoMapper e PontoFidelidadeMapper são usados DENTRO do ClienteMapper estaticamente

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ClienteRepositorioJpa implements ClienteRepositorio {

    private final ClienteJpaRepository jpaRepositoryInternal;
    // MAPPERS NÃO SÃO MAIS INJETADOS

    public ClienteRepositorioJpa(ClienteJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
    }

    // Método auxiliar reconstruirAgregadoCliente não precisa mais de 'this' para os mappers
    private static Cliente reconstruirAgregadoCliente(com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa clienteJpa) {
        if (clienteJpa == null) return null;
        return ClienteMapper.toDomainEntity(clienteJpa); // Chamada estática
    }

    private static List<Cliente> reconstruirListaAgregadosCliente(List<com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa> clientesJpa) {
        return clientesJpa.stream()
                .map(ClienteRepositorioJpa::reconstruirAgregadoCliente) // Referência a método estático
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Cliente salvar(Cliente clienteDominio) {
        // Chamada estática para o mapper
        com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa clienteJpa = ClienteMapper.toJpaEntity(clienteDominio);

        // A lógica de setar o cliente nas coleções filhas (creditosCompensacaoJpa, pontosFidelidadeJpa)
        // já está dentro de ClienteMapper.toJpaEntity, como mostramos acima.

        com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa salvoJpa = jpaRepositoryInternal.save(clienteJpa);
        return reconstruirAgregadoCliente(salvoJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(ClienteId clienteIdDominio) {
        // Chamada estática para o mapper
        UUID idPrimitivo = ClienteMapper.toPrimitiveId(clienteIdDominio);
        return jpaRepositoryInternal.findById(idPrimitivo)
                .map(ClienteRepositorioJpa::reconstruirAgregadoCliente); // Referência a método estático
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return jpaRepositoryInternal.findByCpf(cpf)
                .map(ClienteRepositorioJpa::reconstruirAgregadoCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        return jpaRepositoryInternal.findByEmail(email)
                .map(ClienteRepositorioJpa::reconstruirAgregadoCliente);
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