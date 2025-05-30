package com.cinetech.api.aplicacao;

import com.cinetech.api.dominio.enums.StatusAvaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.Avaliacao;
import com.cinetech.api.dominio.modelos.avaliacao.AvaliacaoId;
import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.repositorios.AvaliacaoRepositorio;
import com.cinetech.api.dominio.repositorios.ClienteRepositorio;
import com.cinetech.api.dominio.repositorios.FilmeRepositorio;
import com.cinetech.api.dominio.repositorios.IngressoRepositorio;
import com.cinetech.api.dominio.servicos.FiltroConteudoServico.FiltroConteudoServico;
import com.cinetech.api.infraestrutura.web.dto.avaliacao.CriarAvaliacaoRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal; // Para calcular média
import java.math.RoundingMode; // Para calcular média


@Service
public class AvaliacaoAplicacao {

    private final AvaliacaoRepositorio avaliacaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final IngressoRepositorio ingressoRepositorio;
    private final FiltroConteudoServico filtroConteudoService;
    // private final AvaliacaoMapper avaliacaoMapper; // Se retornar DTOs

    public AvaliacaoAplicacao(AvaliacaoRepositorio avaliacaoRepositorio,
                              FilmeRepositorio filmeRepositorio,
                              ClienteRepositorio clienteRepositorio,
                              IngressoRepositorio ingressoRepositorio,
                              FiltroConteudoServico filtroConteudoService
            /*, AvaliacaoMapper avaliacaoMapper */) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.ingressoRepositorio = ingressoRepositorio;
        this.filtroConteudoService = filtroConteudoService;
        // this.avaliacaoMapper = avaliacaoMapper;
    }

    /**
     * Caso de Uso: Cliente submete uma avaliação para um filme. (F8)
     * "A avaliação (nota e comentário) só pode ser enviada se o cliente tiver um ingresso validado para aquele filme." (Source 20)
     * "O sistema deve usar um filtro de palavras ofensivas e ocultar automaticamente comentários inapropriados." (Source 21)
     */
    @Transactional
    public Avaliacao submeterAvaliacao(CriarAvaliacaoRequestDTO request) {
        Objects.requireNonNull(request, "Dados da requisição de avaliação não podem ser nulos.");
        FilmeId filmeId = FilmeId.de(request.getFilmeId());
        ClienteId clienteId = ClienteId.de(request.getClienteId());

        // Validações de aplicação
        Cliente cliente = clienteRepositorio.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
        Filme filme = filmeRepositorio.buscarPorId(filmeId)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado: " + filmeId));

        // Regra F8: "A avaliação ... só pode ser enviada se o cliente tiver um ingresso validado para aquele filme."
        boolean assistiuAoFilme = ingressoRepositorio.buscarPorClienteId(clienteId).stream()
                .filter(ingresso -> ingresso.ehParaOFilme(filmeId) && ingresso.isValidadoNaEntrada())
                .findAny().isPresent();

        if (!assistiuAoFilme) {
            throw new IllegalStateException("Cliente " + clienteId + " não pode avaliar o filme " + filmeId +
                    " pois não possui um ingresso validado para ele.");
        }

        // Regra: Cliente só pode avaliar um filme uma vez (verificar se já existe avaliação)
        if (avaliacaoRepositorio.buscarPorClienteEFilme(clienteId, filmeId).isPresent()) {
            throw new IllegalStateException("Cliente " + clienteId + " já avaliou o filme " + filmeId + ".");
        }

        // F8: Filtro de conteúdo ofensivo
        StatusAvaliacao statusSugeridoPeloFiltro = filtroConteudoService.analisarComentarioESugerirStatus(request.getComentario());

        Avaliacao novaAvaliacao = new Avaliacao(
                filmeId,
                clienteId,
                request.getNota(),
                request.getComentario()
        );
        // Se o filtro já define o status final (ex: REPROVADA_OFENSIVA), podemos setá-lo aqui.
        // Se o filtro apenas sugere PENDENTE ou APROVADA, a entidade já começa como PENDENTE.
        // A entidade Avaliacao já define o status inicial como PENDENTE_MODERACAO.
        // Se o filtro já reprovou, atualizamos.
        if (statusSugeridoPeloFiltro == StatusAvaliacao.REPROVADA_OFENSIVA) {
            novaAvaliacao.marcarComoConteudoOfensivo(); // Método da entidade Avaliacao
        } else if (statusSugeridoPeloFiltro == StatusAvaliacao.APROVADA) {
            // Se a política for aprovar automaticamente se não for ofensivo:
            novaAvaliacao.aprovarVisibilidade();
        }
        // Se o statusSugeridoPeloFiltro for PENDENTE_MODERACAO, o status inicial da entidade já é esse.


        Avaliacao avaliacaoSalva = avaliacaoRepositorio.salvar(novaAvaliacao);

        // Recalcular e atualizar a nota média do filme (F8 - Source 22)
        // Esta lógica pode ser um evento ou um processo separado também.
        atualizarNotaMediaFilme(filme); // Passa a entidade Filme carregada

        return avaliacaoSalva;
    }

    /**
     * Método auxiliar para atualizar a nota média de um filme após nova avaliação.
     */
    private void atualizarNotaMediaFilme(Filme filme) {
        List<Avaliacao> avaliacoesAprovadas = avaliacaoRepositorio.buscarAvaliacoesAprovadasPorFilmeId(filme.getId());
        if (!avaliacoesAprovadas.isEmpty()) {
            double somaNotas = avaliacoesAprovadas.stream().mapToInt(Avaliacao::getNota).sum();
            double novaMedia = somaNotas / avaliacoesAprovadas.size();

            // Arredondar para 1 casa decimal
            BigDecimal bdMedia = BigDecimal.valueOf(novaMedia).setScale(1, RoundingMode.HALF_UP);
            filme.setNotaMediaAvaliacao(bdMedia.doubleValue()); // Método da entidade Filme
        } else {
            filme.setNotaMediaAvaliacao(0.0); // Sem avaliações aprovadas, nota é 0
        }
        filmeRepositorio.salvar(filme); // Salva o filme com a nota atualizada

        // F8: "Filmes com média abaixo de 2.5/5 podem ser removidos antecipadamente da exibição." (Source 22)
        // O Application Service FilmeAplicacao teria uma rotina para verificar isso.
        // if (filme.deveSerRemovidoPorNotaBaixa() && !filme.isRemovidoDaProgramacao()) {
        //     // Chamar FilmeAplicacao.processarRemocaoFilmePorNotaBaixa(filme.getId());
        //     // ou emitir um evento.
        // }
    }


    @Transactional(readOnly = true)
    public List<Avaliacao> listarAvaliacoesAprovadasPorFilme(FilmeId filmeId) {
        Objects.requireNonNull(filmeId, "ID do Filme não pode ser nulo.");
        return avaliacaoRepositorio.buscarAvaliacoesAprovadasPorFilmeId(filmeId);
    }

    // Casos de uso para administração/moderação de avaliações
    @Transactional
    public Avaliacao aprovarAvaliacaoManualmente(AvaliacaoId avaliacaoId) {
        Avaliacao avaliacao = avaliacaoRepositorio.buscarPorId(avaliacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação com ID " + avaliacaoId + " não encontrada."));
        avaliacao.aprovarVisibilidade();
        return avaliacaoRepositorio.salvar(avaliacao);
    }

    @Transactional
    public Avaliacao reprovarAvaliacaoManualmente(AvaliacaoId avaliacaoId) {
        Avaliacao avaliacao = avaliacaoRepositorio.buscarPorId(avaliacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação com ID " + avaliacaoId + " não encontrada."));
        // Usar um status genérico de reprovada ou manter o de ofensiva
        avaliacao.marcarComoConteudoOfensivo(); // Ou um novo método `reprovarManualmente()` na entidade
        return avaliacaoRepositorio.salvar(avaliacao);
    }
}
