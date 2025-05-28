package com.cinetech.api.dominio.repositorios;

import com.cinetech.api.dominio.modelos.sala.Sala;
import com.cinetech.api.dominio.modelos.sala.SalaId;

import java.util.List;
import java.util.Optional;

public interface SalaRepositorio {

    /**
     * Salva ou atualiza uma sala.
     * @param sala A entidade Sala a ser salva.
     * @return A entidade Sala salva.
     */
    Sala salvar(Sala sala);

    /**
     * Busca uma sala pelo seu ID.
     * @param salaId O ID da sala.
     * @return Um Optional contendo a Sala se encontrada.
     */
    Optional<Sala> buscarPorId(SalaId salaId);

    /**
     * Busca uma sala pelo seu nome.
     * A busca pode ser case-insensitive.
     * @param nome O nome da sala.
     * @return Um Optional contendo a Sala se encontrada.
     */
    Optional<Sala> buscarPorNome(String nome);

    /**
     * Lista todas as salas cadastradas.
     * @return Uma lista de todas as salas.
     */
    List<Sala> buscarTodas();

    /**
     * Lista todas as salas que estão disponíveis para reserva de eventos.
     * @return Uma lista de salas disponíveis para eventos.
     */
    List<Sala> buscarSalasDisponiveisParaEventos();

    // void deletarPorId(SalaId salaId);
}
