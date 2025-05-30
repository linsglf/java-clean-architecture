package com.cinetech.api.aplicacao;

import com.cinetech.api.dominio.enums.StatusAssento;
import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.enums.TipoExibicao;
import com.cinetech.api.dominio.modelos.assento.Assento;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.modelos.sala.Sala;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.dominio.repositorios.*;
import com.cinetech.api.dominio.servicos.AgendamentoServico.AgendamentoServico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessaoAplicacao {

    private final SessaoRepositorio sessaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private final SalaRepositorio salaRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final AgendamentoServico agendamentoService;
    private final IngressoRepositorio ingressoRepositorio; // Para F4
    // private final CreditoAplicacao creditoAplicacao; // Para F4

    public static final int TEMPO_RESERVA_ASSENTO_MINUTOS = 10; // Configuração

    public SessaoAplicacao(SessaoRepositorio sessaoRepositorio,
                           FilmeRepositorio filmeRepositorio,
                           SalaRepositorio salaRepositorio,
                           ClienteRepositorio clienteRepositorio,
                           AgendamentoServico agendamentoService,
                           IngressoRepositorio ingressoRepositorio
            /*, CreditoAplicacao creditoAplicacao */) {
        this.sessaoRepositorio = sessaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.salaRepositorio = salaRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.agendamentoService = agendamentoService;
        this.ingressoRepositorio = ingressoRepositorio;
        // this.creditoAplicacao = creditoAplicacao;
    }

    /**
     * Caso de Uso: Agendar uma nova sessão (Source 6, Regra "sessões não podem ser sobrepostas" Source 28).
     *
     * @param filmeId ID do filme.
     * @param salaId ID da sala.
     * @param dataHoraInicio Data e hora de início da sessão.
     * @param tipoExibicao Tipo de exibição (2D, 3D).
     * @param precoBaseIngresso Preço base do ingresso para esta sessão.
     * @return A entidade Sessao criada.
     * @throws IllegalArgumentException Se filme ou sala não forem encontrados, ou dados inválidos.
     * @throws IllegalStateException Se houver conflito de horário.
     */
    @Transactional
    public Sessao agendarNovaSessao(FilmeId filmeId, SalaId salaId, LocalDateTime dataHoraInicio,
                                    TipoExibicao tipoExibicao, BigDecimal precoBaseIngresso) {
        Objects.requireNonNull(filmeId, "ID do Filme não pode ser nulo.");
        Objects.requireNonNull(salaId, "ID da Sala não pode ser nulo.");
        // Outras validações de dataHoraInicio, tipoExibicao, precoBaseIngresso já são feitas
        // no construtor da entidade Sessao.

        Filme filme = filmeRepositorio.buscarPorId(filmeId)
                .orElseThrow(() -> new IllegalArgumentException("Filme com ID " + filmeId + " não encontrado."));
        Sala sala = salaRepositorio.buscarPorId(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala com ID " + salaId + " não encontrada."));

        // Calcula o horário de término estimado da sessão
        LocalDateTime dataHoraFimEstimada = dataHoraInicio.plusMinutes(filme.getDuracaoMinutos());

        // Verificar conflito usando o Domain Service (Source 28, BDD Source 36)
        if (agendamentoService.verificarConflitoAgendamento(salaId, dataHoraInicio, dataHoraFimEstimada, Optional.empty(), Optional.empty())) {
            throw new IllegalStateException("Conflito de horário para a sala " + sala.getNome() +
                    " no período de " + dataHoraInicio + " a " + dataHoraFimEstimada);
        }

        // Validação se o filme está em período de exibição (regra de negócio)
        if (!filme.estaEmExibicao(dataHoraInicio.toLocalDate())) {
            throw new IllegalStateException("Filme '" + filme.getTitulo() + "' não está em período de exibição na data " + dataHoraInicio.toLocalDate());
        }


        Sessao novaSessao = new Sessao(filme, sala, dataHoraInicio, tipoExibicao, precoBaseIngresso);
        // O construtor da Sessao já chama gerarAssentosComBaseNaSala()
        return sessaoRepositorio.salvar(novaSessao);
    }

    /**
     * Caso de Uso: Buscar uma sessão por ID.
     */
    @Transactional(readOnly = true)
    public Optional<Sessao> buscarSessaoPorId(SessaoId sessaoId) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        return sessaoRepositorio.buscarPorId(sessaoId);
    }

    /**
     * Caso de Uso: Visualizar os assentos de uma sessão (Mapa de Assentos - F1, Source 9).
     * Retorna a lista de entidades de domínio Assento.
     */
    @Transactional(readOnly = true)
    public List<Assento> buscarAssentosDaSessao(SessaoId sessaoId) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoId + " não encontrada."));
        return sessao.getAssentos(); // Retorna a cópia imutável da lista de assentos da entidade Sessao
    }

    /**
     * Caso de Uso: Cliente tenta selecionar e reservar temporariamente um assento. (F1)
     */
    @Transactional
    public Assento tentarReservarAssento(SessaoId sessaoId, String identificadorAssento, ClienteId clienteId) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        Objects.requireNonNull(identificadorAssento, "Identificador do assento não pode ser nulo.");
        Objects.requireNonNull(clienteId, "ID do Cliente não pode ser nulo.");

        if (!clienteRepositorio.existePorIdValorPrimitivo(clienteId.getValor())) { // Assume que existePorId recebe o UUID
            throw new IllegalArgumentException("Cliente com ID " + clienteId + " não encontrado.");
        }

        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoId + " não encontrada."));

        Assento assentoReservado = sessao.reservarAssentoTemporariamente(identificadorAssento, clienteId, TEMPO_RESERVA_ASSENTO_MINUTOS);
        sessaoRepositorio.salvar(sessao); // Salva a sessão com o estado do assento atualizado
        return assentoReservado;
    }

    /**
     * Caso de Uso: Confirma a ocupação de um assento após o pagamento. (F1)
     * Este método seria chamado pelo fluxo de conclusão de compra de ingresso.
     */
    @Transactional
    public Assento confirmarOcupacaoAssentoAposPagamento(SessaoId sessaoId, String identificadorAssento, ClienteId clienteId) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        Objects.requireNonNull(identificadorAssento, "Identificador do assento não pode ser nulo.");
        Objects.requireNonNull(clienteId, "ID do Cliente não pode ser nulo.");

        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoId + " não encontrada."));

        // O método confirmarOcupacaoAssento da Sessao já chama verificarEAtualizarStatusLotacao
        Assento assentoConfirmado = sessao.confirmarOcupacaoAssento(identificadorAssento, clienteId);
        sessaoRepositorio.salvar(sessao);
        return assentoConfirmado;
    }

    /**
     * Caso de Uso: Libera um assento que estava temporariamente reservado (ex: cliente desistiu, pagamento falhou). (F1)
     */
    @Transactional
    public void liberarReservaTemporariaAssento(SessaoId sessaoId, String identificadorAssento) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        Objects.requireNonNull(identificadorAssento, "Identificador do assento não pode ser nulo.");

        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoId + " não encontrada."));

        // Encontra o assento específico. A lógica de só liberar se estava RESERVADO_TEMP está no Assento.
        Assento assentoParaLiberar = sessao.buscarAssentoPorIdentificador(identificadorAssento)
                .orElseThrow(() -> new IllegalArgumentException("Assento '" + identificadorAssento + "' não encontrado na sessão."));

        // Verifica se realmente estava reservado temporariamente antes de tentar liberar,
        // ou deixa a entidade Assento lidar com isso.
        if (assentoParaLiberar.getStatus() == StatusAssento.RESERVADO_TEMP) {
            sessao.liberarAssentoPorCancelamentoOuExpiracao(identificadorAssento); // Método da entidade Sessao que chama assento.liberar()
            sessaoRepositorio.salvar(sessao);
        } else {
            System.out.println("WARN APP: Tentativa de liberar assento " + identificadorAssento + " que não estava em reserva temporária. Status: " + assentoParaLiberar.getStatus());
        }
    }


    /**
     * Caso de Uso: Rotina para processar expiração de todas as reservas temporárias de assentos em uma sessão. (F1)
     */
    @Transactional
    public void processarExpiracaoDeReservasTemporariasSessao(SessaoId sessaoId) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoId + " não encontrada."));

        sessao.processarExpiracaoDeTodasAsReservasTemporarias(LocalDateTime.now());
        sessaoRepositorio.salvar(sessao);
    }

    /**
     * Caso de Uso: Verifica e atualiza o status de lotação de uma sessão. (F2)
     * Pode ser chamado após cada ingresso confirmado, ou por uma rotina.
     */
    @Transactional
    public StatusSessao verificarEAtualizarLotacaoSessao(SessaoId sessaoId) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoId + " não encontrada."));

        sessao.verificarEAtualizarStatusLotacao();
        sessaoRepositorio.salvar(sessao);
        return sessao.getStatus();
    }

    /**
     * Caso de Uso: Verifica se uma sessão permite novas compras (para UI). (F2)
     */
    @Transactional(readOnly = true)
    public boolean sessaoPermiteNovasCompras(SessaoId sessaoId) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoId + " não encontrada."));

        return sessao.permiteNovasComprasOuReservas();
    }

    /**
     * Caso de Uso: Cancelar uma sessão (ação administrativa). (F4)
     * Isto também deve disparar a lógica de emissão de créditos para clientes impactados.
     */
    @Transactional
    public void cancelarSessaoAdministrativamente(SessaoId sessaoId) {
        Objects.requireNonNull(sessaoId, "ID da Sessão não pode ser nulo.");
        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoId + " não encontrada."));

        // Antes de cancelar, precisamos dos ingressos vendidos para esta sessão para F4.
        // A lógica de buscar ingressos e chamar o CreditoAplicacao ocorreria aqui.
        // List<Ingresso> ingressosVendidos = ingressoRepositorio.buscarPorSessaoId(sessaoId);

        sessao.cancelar(); // Muda o status da sessão e libera assentos internamente.
        sessaoRepositorio.salvar(sessao);

        // TODO: Lógica para F4 - Emissão Automática de Crédito
        // Esta parte é complexa e provavelmente chamaria outro Application Service (ex: CreditoAplicacao)
        // ou um Domain Service.
        // Ex: creditoAplicacao.emitirCreditosParaSessaoCancelada(sessao, ingressosVendidos);
        System.out.println("INFO APP: Sessão " + sessaoId + " cancelada. Próximo passo seria emitir créditos para clientes.");
    }

    /**
     * Caso de Uso: Listar sessões ativas (PROGRAMADA ou ABERTA) para um filme.
     */
    @Transactional(readOnly = true)
    public List<Sessao> listarSessoesAtivasParaFilme(FilmeId filmeId) {
        Objects.requireNonNull(filmeId, "ID do Filme não pode ser nulo.");
        // O FilmeRepositorio poderia ter um método para validar se o filme existe.
        if (!filmeRepositorio.buscarPorId(filmeId).isPresent()) {
            throw new IllegalArgumentException("Filme com ID " + filmeId + " não encontrado.");
        }
        // O SessaoRepositorio precisaria de um método para buscar por filme e status.
        // Ex: return sessaoRepositorio.buscarPorFilmeIdEStatusIn(filmeId, List.of(StatusSessao.PROGRAMADA, StatusSessao.ABERTA));
        // Por ora, uma simulação filtrando em memória (NÃO FAZER EM PRODUÇÃO com muitos dados):
        return sessaoRepositorio.buscarTodas().stream()
                .filter(s -> s.getFilme().getId().equals(filmeId) &&
                        (s.getStatus() == StatusSessao.PROGRAMADA || s.getStatus() == StatusSessao.ABERTA) &&
                        s.getDataHoraInicio().isAfter(LocalDateTime.now())) // Apenas futuras
                .collect(Collectors.toList());
    }
}
