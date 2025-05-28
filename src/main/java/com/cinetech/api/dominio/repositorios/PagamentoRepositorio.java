package com.cinetech.api.dominio.repositorios;

import com.cinetech.api.dominio.enums.StatusPagamento;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.pagamento.Pagamento;
import com.cinetech.api.dominio.modelos.pagamento.PagamentoId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepositorio {

    /**
     * Salva ou atualiza um pagamento.
     * @param pagamento A entidade Pagamento a ser salva.
     * @return A entidade Pagamento salva.
     */
    Pagamento salvar(Pagamento pagamento);

    /**
     * Busca um pagamento pelo seu ID.
     * @param pagamentoId O ID do pagamento.
     * @return Um Optional contendo o Pagamento se encontrado.
     */
    Optional<Pagamento> buscarPorId(PagamentoId pagamentoId);

    /**
     * Busca um pagamento associado a um IngressoId específico.
     * @param ingressoId O ID do ingresso.
     * @return Um Optional contendo o Pagamento se encontrado.
     */
    Optional<Pagamento> buscarPorIngressoId(IngressoId ingressoId);

    /**
     * Busca um pagamento associado a uma ReservaEventoId específica.
     * @param reservaEventoId O ID da reserva de evento.
     * @return Um Optional contendo o Pagamento se encontrado.
     */
    Optional<Pagamento> buscarPorReservaEventoId(ReservaEventoId reservaEventoId);

    /**
     * Busca pagamentos por um ID de transação do gateway externo.
     * @param idTransacaoGateway O ID da transação no gateway.
     * @return Um Optional contendo o Pagamento se encontrado (pode haver múltiplos se o ID não for único globalmente, mas geralmente é).
     */
    Optional<Pagamento> buscarPorIdTransacaoGateway(String idTransacaoGateway);

    /**
     * Lista todos os pagamentos com um determinado status.
     * @param status O status do pagamento.
     * @return Uma lista de pagamentos com o status especificado.
     */
    List<Pagamento> buscarPorStatus(StatusPagamento status);

    /**
     * Lista todos os pagamentos.
     * @return Uma lista de todos os pagamentos.
     */
    List<Pagamento> buscarTodos();
}
