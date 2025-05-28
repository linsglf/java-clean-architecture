package com.cinetech.api.dominio.repositorios;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.ingresso.Ingresso;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;

import java.util.List;
import java.util.Optional;

public interface IngressoRepositorio {

    /**
     * Salva um novo ingresso no repositório.
     * @param ingresso A entidade Ingresso a ser salva.
     * @return A entidade Ingresso salva.
     */
    Ingresso salvar(Ingresso ingresso);

    /**
     * Busca um ingresso pelo seu ID.
     * @param ingressoId O ID do ingresso.
     * @return Um Optional contendo o Ingresso se encontrado.
     */
    Optional<Ingresso> buscarPorId(IngressoId ingressoId);

    /**
     * Busca um ingresso pelo seu código de validação único.
     * @param codigoValidacao O código de validação do ingresso.
     * @return Um Optional contendo o Ingresso se encontrado.
     */
    Optional<Ingresso> buscarPorCodigoValidacao(String codigoValidacao);

    /**
     * Lista todos os ingressos comprados para uma determinada sessão.
     * @param sessaoId O ID da sessão.
     * @return Uma lista de ingressos para a sessão.
     */
    List<Ingresso> buscarPorSessaoId(SessaoId sessaoId);

    /**
     * Lista todos os ingressos comprados por um determinado cliente.
     * @param clienteId O ID do cliente.
     * @return Uma lista de ingressos do cliente.
     */
    List<Ingresso> buscarPorClienteId(ClienteId clienteId);

    /**
     * Lista todos os ingressos.
     * @return Uma lista de todos os ingressos.
     */
    List<Ingresso> buscarTodos();

    // Outros métodos podem ser úteis, ex: buscar ingressos por período de compra, etc.
    // void deletarPorId(IngressoId ingressoId); // Geralmente ingressos não são deletados, mas cancelados/invalidados
}
