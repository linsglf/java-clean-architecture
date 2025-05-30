package com.cinetech.api.dominio.repositorios;

import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.filme.FilmeId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FilmeRepositorio {

    /**
     * Salva ou atualiza um filme no repositório.
     * @param filme A entidade Filme a ser salva.
     * @return A entidade Filme salva.
     */
    Filme salvar(Filme filme);

    /**
     * Busca um filme pelo seu ID.
     * @param filmeId O ID do filme.
     * @return Um Optional contendo o Filme se encontrado.
     */
    Optional<Filme> buscarPorId(FilmeId filmeId);

    /**
     * Busca um filme pelo seu título.
     * A busca pode ser case-insensitive dependendo da implementação.
     * @param titulo O título do filme.
     * @return Um Optional contendo o Filme se encontrado.
     */
    Optional<Filme> buscarPorTitulo(String titulo);

    /**
     * Lista todos os filmes cadastrados.
     * @return Uma lista de todos os filmes.
     */
    List<Filme> buscarTodos();

    /**
     * Lista todos os filmes que estão atualmente em período de exibição
     * (dataReferencia >= dataInicioExibicao E dataReferencia <= dataFimExibicao)
     * e não foram marcados como removidos da programação.
     * @param dataReferencia A data para a qual verificar a exibição.
     * @return Uma lista de filmes em exibição.
     */
    List<Filme> buscarFilmesEmExibicao(LocalDate dataReferencia);

    /**
     * Lista filmes cujo período de exibição terminou antes da data de referência
     * e que ainda não foram marcados como removidos da programação.
     * Útil para a funcionalidade F3 (Remover automaticamente filmes com tempo de exibição expirado).
     * @param dataReferencia A data de referência.
     * @return Lista de filmes com exibição expirada.
     */
    List<Filme> buscarFilmesComExibicaoExpiradaParaRemocao(LocalDate dataReferencia);

    /**
     * Deleta um filme pelo seu ID.
     * @param filmeId O ID do filme a ser deletado.
     */
    void deletarPorId(FilmeId filmeId);

    boolean existePorTitulo(String titulo);
}
