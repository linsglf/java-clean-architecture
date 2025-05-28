package com.cinetech.api.dominio.repositorios;

import com.cinetech.api.dominio.enums.StatusAvaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.Avaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.AvaliacaoId;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.filme.FilmeId;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepositorio {

    /**
     * Salva ou atualiza uma avaliação.
     * @param avaliacao A entidade Avaliacao a ser salva.
     * @return A entidade Avaliacao salva.
     */
    Avaliacao salvar(Avaliacao avaliacao);

    /**
     * Busca uma avaliação pelo seu ID.
     * @param avaliacaoId O ID da avaliação.
     * @return Um Optional contendo a Avaliacao se encontrada.
     */
    Optional<Avaliacao> buscarPorId(AvaliacaoId avaliacaoId);

    /**
     * Lista todas as avaliações para um determinado filme.
     * @param filmeId O ID do filme.
     * @return Uma lista de avaliações para o filme.
     */
    List<Avaliacao> buscarPorFilmeId(FilmeId filmeId);

    /**
     * Busca a avaliação de um cliente específico para um filme específico.
     * Útil para verificar se o cliente já avaliou o filme.
     * @param clienteId O ID do cliente.
     * @param filmeId O ID do filme.
     * @return Um Optional contendo a Avaliacao se encontrada.
     */
    Optional<Avaliacao> buscarPorClienteEFilme(ClienteId clienteId, FilmeId filmeId);

    /**
     * Lista todas as avaliações aprovadas para um determinado filme.
     * Útil para exibir na página do filme (F8).
     * @param filmeId O ID do filme.
     * @return Uma lista de avaliações aprovadas para o filme.
     */
    List<Avaliacao> buscarAvaliacoesAprovadasPorFilmeId(FilmeId filmeId);

    /**
     * Lista todas as avaliações com um determinado status.
     * Útil para moderação (ex: buscar PENDENTE_MODERACAO).
     * @param status O status da avaliação.
     * @return Uma lista de avaliações com o status especificado.
     */
    List<Avaliacao> buscarPorStatus(StatusAvaliacao status);

    /**
     * Lista todas as avaliações.
     * @return Uma lista de todas as avaliações.
     */
    List<Avaliacao> buscarTodas();

    // void deletarPorId(AvaliacaoId avaliacaoId);
}
