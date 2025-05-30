package com.cinetech.api.aplicacao;

import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.repositorios.FilmeRepositorio;
import com.cinetech.api.dominio.repositorios.SessaoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FilmeAplicacao {

    private final FilmeRepositorio filmeRepositorio;
    private final SessaoRepositorio sessaoRepositorio; // Para regra de negócio em removerFilmesComExibicaoExpirada

    // Mappers seriam injetados se esta camada retornasse DTOs diretamente.
    // Por enquanto, retorna entidades de domínio ou VOs.

    public FilmeAplicacao(FilmeRepositorio filmeRepositorio, SessaoRepositorio sessaoRepositorio) {
        this.filmeRepositorio = filmeRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    /**
     * Caso de Uso: Cadastrar um novo filme.
     * Referência: Fluxo "Cadastro e Agendamento de Filmes" (Source 5 do Descrição de Domínio).
     */
    @Transactional
    public Filme cadastrarNovoFilme(String titulo, String genero, int duracaoMinutos, String idioma,
                                    String classificacaoEtaria, LocalDate dataInicioExibicao,
                                    LocalDate dataFimExibicao, String sinopse) {
        Objects.requireNonNull(titulo, "Título não pode ser nulo.");
        Objects.requireNonNull(genero, "Gênero não pode ser nulo.");
        // Outras validações de parâmetros básicos podem ser feitas aqui ou assumir que DTOs de entrada já validaram.

        // Regra de aplicação: Verificar se já existe um filme com o mesmo título
        if (filmeRepositorio.buscarPorTitulo(titulo.trim()).isPresent()) {
            throw new IllegalArgumentException("Filme com o título '" + titulo + "' já existe.");
        }

        // A entidade Filme já valida seus próprios campos no construtor
        Filme novoFilme = new Filme(titulo, genero, duracaoMinutos, idioma, classificacaoEtaria,
                dataInicioExibicao, dataFimExibicao, sinopse);
        return filmeRepositorio.salvar(novoFilme);
    }

    /**
     * Caso de Uso: Buscar um filme por seu ID.
     */
    @Transactional(readOnly = true)
    public Optional<Filme> buscarFilmePorId(FilmeId filmeId) {
        Objects.requireNonNull(filmeId, "ID do Filme não pode ser nulo.");
        return filmeRepositorio.buscarPorId(filmeId);
    }

    /**
     * Caso de Uso: Listar todos os filmes cadastrados.
     */
    @Transactional(readOnly = true)
    public List<Filme> listarTodosOsFilmes() {
        return filmeRepositorio.buscarTodos();
    }

    /**
     * Caso de Uso: Listar filmes atualmente em exibição.
     */
    @Transactional(readOnly = true)
    public List<Filme> listarFilmesEmExibicao(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        return filmeRepositorio.buscarFilmesEmExibicao(dataReferencia);
    }


    /**
     * Caso de Uso: Rotina para marcar filmes com exibição expirada como removidos da programação. (F3)
     * "Após o prazo de exibição, o filme é automaticamente removido da programação." (Source 7)
     * Este método é projetado para ser chamado por um Scheduler.
     */
    @Transactional
    public void processarRemocaoFilmesExpirados() {
        LocalDate hoje = LocalDate.now();
        System.out.println("INFO APP: Executando rotina de remoção de filmes expirados em " + hoje);

        List<Filme> filmesExpirados = filmeRepositorio.buscarFilmesComExibicaoExpiradaParaRemocao(hoje);

        for (Filme filme : filmesExpirados) {
            // Regra de Negócio Adicional (Exemplo): Não remover se ainda tiver sessões futuras programadas (erro de dados)
            List<Sessao> sessoesFuturas = sessaoRepositorio.buscarSessoesAtivasPorFilmeId(filme.getId());
            if (!sessoesFuturas.isEmpty()) {
                System.err.println("ALERTA APP: Filme '" + filme.getTitulo() + "' (ID: " + filme.getId() +
                        ") expirou mas ainda possui " + sessoesFuturas.size() +
                        " sessões futuras ativas. Remoção da programação adiada para este filme.");
                // Poderia lançar uma exceção, logar, ou notificar administradores.
                continue;
            }

            filme.marcarComoRemovidoDaProgramacao(); // Método da entidade de domínio
            filmeRepositorio.salvar(filme); // Salva o estado atualizado do filme
            System.out.println("INFO APP: Filme '" + filme.getTitulo() + "' (ID: " + filme.getId() +
                    ") marcado como removido da programação (expirado em " + filme.getDataFimExibicao() + ").");
        }
        System.out.println("INFO APP: Rotina de remoção de filmes expirados concluída. " + filmesExpirados.size() + " filmes expirados foram processados.");
    }

    /**
     * Caso de Uso: Rotina para marcar filmes com avaliação muito baixa como removidos. (F8 - parte da regra)
     * "Filmes com média abaixo de 2.5/5 podem ser removidos antecipadamente da exibição." (Source 22)
     */
    @Transactional
    public void processarRemocaoFilmesPorNotaBaixa() {
        System.out.println("INFO APP: Executando rotina de remoção de filmes por nota baixa.");
        List<Filme> todosOsFilmesAtivos = filmeRepositorio.buscarFilmesEmExibicao(LocalDate.now()); // Considera apenas os ativos

        int filmesRemovidos = 0;
        for (Filme filme : todosOsFilmesAtivos) {
            if (filme.deveSerRemovidoPorNotaBaixa()) { // Método da entidade Filme
                // Regra de Negócio Adicional: Verificar se há sessões futuras antes de remover
                List<Sessao> sessoesFuturas = sessaoRepositorio.buscarSessoesAtivasPorFilmeId(filme.getId());
                if (!sessoesFuturas.isEmpty()) {
                    System.err.println("ALERTA APP: Filme '" + filme.getTitulo() + "' (ID: " + filme.getId() +
                            ") tem nota baixa mas ainda possui " + sessoesFuturas.size() +
                            " sessões futuras ativas. Remoção da programação adiada para este filme.");
                    continue;
                }

                filme.marcarComoRemovidoDaProgramacao();
                filmeRepositorio.salvar(filme);
                System.out.println("INFO APP: Filme '" + filme.getTitulo() + "' (ID: " + filme.getId() +
                        ") marcado como removido da programação devido à nota média baixa: " + filme.getNotaMediaAvaliacao());
                filmesRemovidos++;
            }
        }
        System.out.println("INFO APP: Rotina de remoção de filmes por nota baixa concluída. " + filmesRemovidos + " filmes processados para remoção.");
    }

    // Outros Casos de Uso para filme que tu pode implementar:
    // - atualizarDadosFilme(FilmeId filmeId, AtualizarFilmeRequestDTO dto)
    // - buscarFilmesPorGenero(String genero)
}
