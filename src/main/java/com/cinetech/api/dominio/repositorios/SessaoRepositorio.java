package com.cinetech.api.dominio.repositorios;

import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoRepositorio {

    /**
     * Salva ou atualiza uma sessão no repositório.
     * @param sessao A entidade Sessao a ser salva.
     * @return A entidade Sessao salva (pode ter ID atualizado se for nova).
     */
    Sessao salvar(Sessao sessao);

    /**
     * Busca uma sessão pelo seu ID.
     * @param sessaoId O ID da sessão a ser buscada.
     * @return Um Optional contendo a Sessao se encontrada, ou Optional.empty() caso contrário.
     */
    Optional<Sessao> buscarPorId(SessaoId sessaoId);

    /**
     * Lista todas as sessões cadastradas.
     * (Pode precisar de paginação em um sistema real).
     * @return Uma lista de todas as sessões.
     */
    List<Sessao> buscarTodas();

    /**
     * Busca todas as sessões associadas a uma determinada SalaId.
     * @param salaId O ID da sala.
     * @return Uma lista de sessões para a sala especificada.
     */
    List<Sessao> buscarPorSalaId(SalaId salaId);

    /**
     * Busca todas as sessões ativas (PROGRAMADA ou ABERTA) para um determinado FilmeId.
     * @param filmeId O ID do filme.
     * @return Uma lista de sessões ativas para o filme.
     */
    List<Sessao> buscarSessoesAtivasPorFilmeId(FilmeId filmeId);


    /**
     * Busca sessões em uma determinada sala que ocorrem (total ou parcialmente)
     * dentro de um período especificado.
     * Permite excluir uma SessaoId específica da busca (útil ao verificar conflito para uma sessão existente).
     *
     * A lógica de "conflito" é:
     * (inicioSessaoExistente < fimProposto) AND (fimSessaoExistente > inicioProposto)
     * onde fimSessaoExistente = inicioSessaoExistente + duracaoFilme.
     *
     * @param salaId O ID da sala.
     * @param inicioPeriodo O início do período de verificação.
     * @param fimPeriodo O fim do período de verificação.
     * @param sessaoIdParaExcluir Opcional: ID de uma sessão a ser ignorada na busca.
     * @return Uma lista de sessões que conflitam com o período na sala.
     */
    List<Sessao> buscarSessoesConflitantesPorSalaEPeriodo(
            SalaId salaId,
            LocalDateTime inicioPeriodo,
            LocalDateTime fimPeriodo,
            Optional<SessaoId> sessaoIdParaExcluir
    );

    /**
     * Busca sessoes por status.
     * @param status O status da sessão a ser buscado.
     * @return Uma lista de sessoes com o status especificado.
     */
    List<Sessao> buscarPorStatus(StatusSessao status);

    // Outros métodos podem ser necessários, como:
    // void deletarPorId(SessaoId sessaoId);
    // List<Sessao> buscarPorData(LocalDate data);
}
