package com.cinetech.api.dominio.modelos.sessao;

import com.cinetech.api.dominio.enums.TipoAssento;
import com.cinetech.api.dominio.enums.TipoExibicao;
import com.cinetech.api.dominio.modelos.assento.Assento;
import com.cinetech.api.dominio.modelos.assento.AssentoId;
import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.enums.StatusAssento;
import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.modelos.filme.Filme;
import com.cinetech.api.dominio.modelos.sala.Sala;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Sessao {
    private final SessaoId id;
    private Filme filme;
    private Sala sala;
    private LocalDateTime dataHoraInicio;
    private TipoExibicao tipoExibicao; // "2D/3D" [cite: 4]
    private BigDecimal precoIngressoBase;
    private StatusSessao status;
    private final List<Assento> assentos;

    // Construtor para nova sessão
    public Sessao(Filme filme, Sala sala, LocalDateTime dataHoraInicio, TipoExibicao tipoExibicao, BigDecimal precoIngressoBase) {
        this(SessaoId.novo(), filme, sala, dataHoraInicio, tipoExibicao, precoIngressoBase, StatusSessao.PROGRAMADA, new ArrayList<>());
        gerarAssentosComBaseNaSala(); // Popula os assentos
    }

    // Construtor completo para reconstituição
    public Sessao(SessaoId id, Filme filme, Sala sala, LocalDateTime dataHoraInicio, TipoExibicao tipoExibicao,
                  BigDecimal precoIngressoBase, StatusSessao status, List<Assento> assentosExistentes) {
        this.id = Objects.requireNonNull(id, "ID da Sessão não pode ser nulo.");
        setFilme(filme);
        setSala(sala);
        setDataHoraInicio(dataHoraInicio);
        setTipoExibicao(tipoExibicao);
        setPrecoIngressoBase(precoIngressoBase);
        this.status = Objects.requireNonNull(status, "Status da Sessão não pode ser nulo.");
        this.assentos = new ArrayList<>(Objects.requireNonNull(assentosExistentes, "Lista de assentos não pode ser nula."));
        // Valida se os assentos existentes realmente pertencem a esta sessão (se Assento tiver back-reference)
        // Como Assento tem `final Sessao sessao`, o construtor de Assento já faria essa ligação.
    }

    // Getters
    public SessaoId getId() { return id; }
    public Filme getFilme() { return filme; }
    public Sala getSala() { return sala; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public TipoExibicao getTipoExibicao() { return tipoExibicao; }
    public BigDecimal getPrecoIngressoBase() { return precoIngressoBase; }
    public StatusSessao getStatus() { return status; }
    public List<Assento> getAssentos() { return Collections.unmodifiableList(assentos); }

    // Setters (controlados)
    public void setFilme(Filme filme) {
        this.filme = Objects.requireNonNull(filme, "Filme da sessão não pode ser nulo.");
    }

    public void setSala(Sala sala) {
        // Mudar a sala de uma sessão existente com assentos já definidos/vendidos é complexo.
        // Normalmente, isso implicaria cancelar a sessão atual e criar uma nova.
        // Por ora, permitimos, mas a lógica de recriar assentos/invalidar ingressos seria necessária.
        if (this.assentos != null && !this.assentos.isEmpty() && (this.sala != null && !this.sala.getId().equals(sala.getId()))) {
            throw new IllegalStateException("Não é possível alterar a sala de uma sessão que já possui assentos configurados/vendidos. Cancele e crie uma nova sessão.");
        }
        this.sala = Objects.requireNonNull(sala, "Sala da sessão não pode ser nula.");
        // Se os assentos ainda não foram gerados, este seria um bom momento.
        // if (this.assentos.isEmpty()) { gerarAssentosComBaseNaSala(); }
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = Objects.requireNonNull(dataHoraInicio, "Data e hora de início não podem ser nulos.");
        // Validação de conflito com outras sessões na mesma sala (Source 28, 36)
        // é uma regra de aplicação/domain service, pois requer consulta a outras Sessões.
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = Objects.requireNonNull(tipoExibicao, "Tipo de exibição não pode ser nulo.");
    }

    public void setPrecoIngressoBase(BigDecimal precoIngressoBase) {
        if (precoIngressoBase == null || precoIngressoBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço base do ingresso não pode ser nulo ou negativo.");
        }
        this.precoIngressoBase = precoIngressoBase;
    }


    // Métodos de Negócio

    private void gerarAssentosComBaseNaSala() {
        this.assentos.clear(); // Limpa assentos existentes se houver (cuidado se já houve vendas)
        if (this.sala == null) {
            throw new IllegalStateException("Sala não definida para gerar assentos.");
        }
        // Lógica simplificada de geração de assentos. Uma sala real teria um layout.
        // Ex: 10 fileiras (A-J) de 10 cadeiras (1-10) = 100 assentos
        // A informação de quais são COMUM, VIP, PCD viria do layout da Sala [cite: 4, 13]
        // Aqui, vamos criar todos como COMUM por simplicidade.
        int capacidade = this.sala.getCapacidadeTotal();
        for (int i = 0; i < capacidade; i++) {
            // Lógica para gerar identificador (ex: "A1", "A2", ...)
            String identificador = "Assento-" + (i + 1);
            // Tipo de assento viria do layout da sala.
            this.assentos.add(new Assento(this, identificador, TipoAssento.COMUM));
        }
        System.out.println("INFO DOMINIO: " + this.assentos.size() + " assentos gerados para sessão " + this.id + " na sala " + this.sala.getNome());
    }

    public Optional<Assento> buscarAssentoPorIdentificador(String identificadorAssento) {
        if (identificadorAssento == null || identificadorAssento.trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador do assento não pode ser vazio.");
        }
        return this.assentos.stream()
                .filter(a -> a.getIdentificadorPosicao().equalsIgnoreCase(identificadorAssento.trim()))
                .findFirst();
    }

    public Assento reservarAssentoTemporariamente(String identificadorAssento, ClienteId clienteId, int minutosParaExpirar) { // F1
        // Correção aqui:
        if (!permiteNovasComprasOuReservas()) {
            throw new IllegalStateException("Sessão ID " + this.id + " não está aberta para novas reservas (status: " + this.status + ").");
        }
        Assento assento = buscarAssentoPorIdentificador(identificadorAssento)
                .orElseThrow(() -> new IllegalArgumentException("Assento '" + identificadorAssento + "' não encontrado nesta sessão."));

        assento.reservarTemporariamente(clienteId, minutosParaExpirar);
        // A lógica de verificarEAtualizarStatusLotacao() é chamada após uma *confirmação* de ocupação.
        // A simples reserva temporária não lota a sessão em termos de venda finalizada.
        return assento;
    }

    public Assento confirmarOcupacaoAssento(String identificadorAssento, ClienteId clienteIdQueReservou) { // F1
        if (this.status == StatusSessao.CANCELADA || this.status == StatusSessao.FINALIZADA || this.status == StatusSessao.LOTADA) {
            throw new IllegalStateException("Não é possível confirmar assento em sessão com status: " + this.status);
        }
        Assento assento = buscarAssentoPorIdentificador(identificadorAssento)
                .orElseThrow(() -> new IllegalArgumentException("Assento '" + identificadorAssento + "' não encontrado nesta sessão."));

        // Validação se o assento estava reservado para este cliente (opcional, mas boa prática)
        if (assento.getStatus() == StatusAssento.RESERVADO_TEMP &&
                (assento.getClienteIdReservaTemporaria() == null || !assento.getClienteIdReservaTemporaria().equals(clienteIdQueReservou))) {
            throw new IllegalStateException("Assento '" + identificadorAssento + "' estava reservado temporariamente por outro cliente.");
        }

        assento.confirmarOcupacaoDefinitiva();
        verificarEAtualizarStatusLotacao(); // F2
        return assento;
    }

    public void liberarAssentoPorCancelamentoOuExpiracao(String identificadorAssento) { // F1
        Assento assento = buscarAssentoPorIdentificador(identificadorAssento)
                .orElseThrow(() -> new IllegalArgumentException("Assento '" + identificadorAssento + "' não encontrado nesta sessão."));
        assento.liberar();
        // Se a sessão estava lotada, seu status pode mudar.
        if (this.status == StatusSessao.LOTADA) {
            // Reavalia se ainda está lotada ou volta para ABERTA
            verificarEAtualizarStatusLotacao();
        }
    }

    public void processarExpiracaoDeTodasAsReservasTemporarias(LocalDateTime agora) { // F1
        boolean algumaReservaExpirou = false;
        for (Assento assento : this.assentos) {
            if (assento.liberarSeReservaTemporariaExpirada(agora)) {
                algumaReservaExpirou = true;
            }
        }
        if (algumaReservaExpirou && this.status == StatusSessao.LOTADA) {
            verificarEAtualizarStatusLotacao();
        }
    }

    /**
     * Verifica se a sessão está lotada e atualiza seu status. (F2) [cite: 12, 28, 37]
     * Considera um assento como "vendável" se não for BLOQUEADO.
     * A sessão é LOTADA se todos os assentos vendáveis estão OCUPADO_FINAL.
     * Reservas temporárias não contam para lotação total final, mas impedem seleção.
     */
    public void verificarEAtualizarStatusLotacao() {
        if (!podeTerStatusLotacaoAlterado()) return;

        boolean todosVendaveisOcupados = this.assentos.stream()
                .filter(a -> a.getTipo() != TipoAssento.PCD || a.getStatus() != StatusAssento.BLOQUEADO) // Exemplo simples de filtro para assentos "vendáveis"
                .allMatch(a -> a.getStatus() == StatusAssento.OCUPADO_FINAL);

        if (todosVendaveisOcupados) {
            if (this.status != StatusSessao.LOTADA) {
                this.status = StatusSessao.LOTADA;
                System.out.println("INFO DOMINIO: Sessão " + this.id + " marcada como LOTADA.");
                // Gerar evento de domínio: SessaoLotadaEvent(this.id)
            }
        } else {
            if (this.status == StatusSessao.LOTADA) { // Se estava lotada e agora não está mais
                this.status = StatusSessao.ABERTA; // Assume que se não está lotada, está aberta (se já não passou)
                System.out.println("INFO DOMINIO: Sessão " + this.id + " não está mais LOTADA. Status: ABERTA.");
            }
        }
    }

    private boolean podeTerStatusLotacaoAlterado() {
        return this.status != StatusSessao.CANCELADA &&
                this.status != StatusSessao.FINALIZADA &&
                this.status != StatusSessao.EM_ANDAMENTO;
    }


    /**
     * Cancela a sessão. (F4) [cite: 19]
     */
    public void cancelar() {
        if (this.status == StatusSessao.CANCELADA || this.status == StatusSessao.FINALIZADA) {
            throw new IllegalStateException("Sessão com ID " + this.id + " não pode ser cancelada pois seu status é " + this.status);
        }
        // Antes de cancelar, é preciso verificar se há ingressos vendidos para processar créditos (F4).
        // Essa lógica de coordenação (buscar ingressos, emitir créditos) fica no Application Service.
        // A entidade Sessao apenas muda seu próprio estado e o de seus assentos.
        StatusSessao statusAnterior = this.status;
        this.status = StatusSessao.CANCELADA;
        // Libera todos os assentos que estavam ocupados ou reservados temporariamente
        this.assentos.forEach(assento -> {
            if (assento.getStatus() == StatusAssento.OCUPADO_FINAL || assento.getStatus() == StatusAssento.RESERVADO_TEMP) {
                assento.liberar();
            }
        });
        System.out.println("INFO DOMINIO: Sessão " + this.id + " ("+ statusAnterior + ") foi CANCELADA e assentos liberados.");
        // Gerar evento de domínio: SessaoCanceladaEvent(this.id)
    }

    /**
     * Verifica se a sessão permite novas compras/reservas. (F2) [cite: 12]
     */
    public boolean permiteNovasComprasOuReservas() {
        if (this.status == StatusSessao.LOTADA ||
                this.status == StatusSessao.CANCELADA ||
                this.status == StatusSessao.EM_ANDAMENTO ||
                this.status == StatusSessao.FINALIZADA) {
            return false;
        }
        // Não permite compra se já passou do horário de início (ou uma pequena tolerância)
        return LocalDateTime.now().isBefore(this.dataHoraInicio);
    }

    /**
     * Determina o preço final de um ingresso para esta sessão para um dado cliente,
     * considerando o perfil do cliente para meia-entrada (F5).
     * Promoções mais complexas (horário, etc.) seriam tratadas por um Application Service
     * que consulta entidades Promocao.
     */
    public BigDecimal calcularPrecoParaCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo para cálculo de preço.");
        if (cliente.elegivelParaMeiaEntrada()) { // F5 [cite: 16]
            return this.precoIngressoBase.multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.HALF_UP);
        }
        return this.precoIngressoBase;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sessao sessao = (Sessao) o;
        return id.equals(sessao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sessao{" +
                "id=" + id +
                ", filme=" + (filme != null ? filme.getTitulo() : "N/A") +
                ", sala=" + (sala != null ? sala.getNome() : "N/A") +
                ", dataHoraInicio=" + dataHoraInicio +
                ", status=" + status +
                ", tipoExibicao=" + tipoExibicao +
                '}';
    }
}
