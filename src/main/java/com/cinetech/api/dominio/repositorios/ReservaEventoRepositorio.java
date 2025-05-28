package com.cinetech.api.dominio.repositorios;

import com.cinetech.api.dominio.enums.StatusReservaEvento;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEvento;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.modelos.sala.SalaId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaEventoRepositorio {

    /**
     * Salva ou atualiza uma reserva de evento.
     * @param reservaEvento A entidade ReservaEvento a ser salva.
     * @return A entidade ReservaEvento salva.
     */
    ReservaEvento salvar(ReservaEvento reservaEvento);

    /**
     * Busca uma reserva de evento pelo seu ID.
     * @param reservaEventoId O ID da reserva a ser buscada.
     * @return Um Optional contendo a ReservaEvento se encontrada.
     */
    Optional<ReservaEvento> buscarPorId(ReservaEventoId reservaEventoId);

    /**
     * Lista todas as reservas de evento.
     * @return Uma lista de todas as reservas de evento.
     */
    List<ReservaEvento> buscarTodas();

    /**
     * Busca todas as reservas de evento para uma determinada SalaId.
     * @param salaId O ID da sala.
     * @return Uma lista de reservas de evento para a sala.
     */
    List<ReservaEvento> buscarPorSalaId(SalaId salaId);

    /**
     * Busca todas as reservas de evento para um determinado ClienteId.
     * @param clienteId O ID do cliente.
     * @return Uma lista de reservas de evento para o cliente.
     */
    List<ReservaEvento> buscarPorClienteId(ClienteId clienteId);

    /**
     * Busca reservas de evento em uma determinada sala que ocorrem (total ou parcialmente)
     * dentro de um período especificado.
     * Permite excluir uma ReservaEventoId específica da busca.
     *
     * @param salaId O ID da sala.
     * @param inicioPeriodo O início do período de verificação.
     * @param fimPeriodo O fim do período de verificação.
     * @param reservaEventoIdParaExcluir Opcional: ID de uma reserva a ser ignorada.
     * @return Uma lista de reservas de evento que conflitam com o período na sala.
     */
    List<ReservaEvento> buscarReservasEventoConflitantesPorSalaEPeriodo(
            SalaId salaId,
            LocalDateTime inicioPeriodo,
            LocalDateTime fimPeriodo,
            Optional<ReservaEventoId> reservaEventoIdParaExcluir
    );

    /**
     * Busca reservas de evento por status.
     * @param status O status da reserva a ser buscado.
     * @return Uma lista de reservas com o status especificado.
     */
    List<ReservaEvento> buscarPorStatus(StatusReservaEvento status);

    // void deletarPorId(ReservaEventoId reservaEventoId);
}
