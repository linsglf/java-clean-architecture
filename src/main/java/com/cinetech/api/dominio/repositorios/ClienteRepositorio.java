package com.cinetech.api.dominio.repositorios;


import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositorio {

    /**
     * Salva ou atualiza um cliente.
     * Inclui a persistência de seus PontoFidelidade e CreditoCompensacao associados.
     * @param cliente A entidade Cliente a ser salva.
     * @return A entidade Cliente salva.
     */
    Cliente salvar(Cliente cliente);

    /**
     * Busca um cliente pelo seu ID.
     * @param clienteId O ID do cliente.
     * @return Um Optional contendo o Cliente se encontrado.
     */
    Optional<Cliente> buscarPorId(ClienteId clienteId);

    /**
     * Busca um cliente pelo seu CPF.
     * @param cpf O CPF do cliente.
     * @return Um Optional contendo o Cliente se encontrado.
     */
    Optional<Cliente> buscarPorCpf(String cpf);

    /**
     * Busca um cliente pelo seu email.
     * @param email O email do cliente.
     * @return Um Optional contendo o Cliente se encontrado.
     */
    Optional<Cliente> buscarPorEmail(String email);

    /**
     * Lista todos os clientes.
     * @return Uma lista de todos os clientes.
     */
    List<Cliente> buscarTodos();

    // void deletarPorId(ClienteId clienteId);
}
