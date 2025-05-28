package com.cinetech.api.dominio.sessao;

import com.cinetech.api.dominio.assento.Assento;
import com.cinetech.api.dominio.assento.AssentoId;
import com.cinetech.api.dominio.cliente.ClienteId;
import com.cinetech.api.dominio.enums.StatusAssento;
import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.filme.Filme;
import com.cinetech.api.dominio.sala.Sala;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sessao {
    private final SessaoId id;
    private Filme filme;
    private Sala sala;
    private LocalDateTime dataHoraInicio;
    private BigDecimal precoIngressoBase;
    private StatusSessao status;
    private final List<Assento> assentos; // Sessão é a raiz do agregado de Assentos

    // Construtor para nova sessão, geralmente popula os assentos baseados na sala
    public Sessao(Filme filme, Sala sala, LocalDateTime dataHoraInicio, BigDecimal precoIngressoBase) {
        this(SessaoId.novo(), filme, sala, dataHoraInicio, precoIngressoBase, StatusSessao.PROGRAMADA, new ArrayList<>());
        // Lógica para popular this.assentos com base na sala.capacidadeTotal
        // Esta lógica é crucial e pode ser complexa (ex: A1-A10, B1-B10).
        // Por simplicidade, vamos assumir que ela é chamada aqui ou que os assentos são adicionados
        // por um método dedicado após a criação da Sessao.
        // Exemplo: this.gerarAssentosPadrao();
    }

    // Construtor completo para reconstituição (usado pela persistência/mappers)
    public Sessao(SessaoId id, Filme filme, Sala sala, LocalDateTime dataHoraInicio,
                  BigDecimal precoIngressoBase, StatusSessao status, List<Assento> assentos) {
        this.id = Objects.requireNonNull(id, "ID da Sessão não pode ser nulo.");
        setFilme(filme); // Usa setters para validação
        setSala(sala);
        setDataHoraInicio(dataHoraInicio);
        setPrecoIngressoBase(precoIngressoBase);
        this.status = Objects.requireNonNull(status, "Status da Sessão não pode ser nulo.");
        // Garante que a lista de assentos não seja nula e que os assentos referenciem esta sessão.
        this.assentos = new ArrayList<>(Objects.requireNonNull(assentos, "Lista de assentos não pode ser nula."));
        this.assentos.forEach(assento -> {
            if (assento == null) throw new IllegalArgumentException("Um assento na lista não pode ser nulo.");
            // Se Assento guarda uma referência à Sessao, verificar consistência.
            // No nosso modelo atual de Assento, ele recebe Sessao no construtor.
        });
    }

    // Getters
    public SessaoId getId() { return id; }
    public Filme getFilme() { return filme; }
    public Sala getSala() { return sala; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public BigDecimal getPrecoIngressoBase() { return precoIngressoBase; }
    public StatusSessao getStatus() { return status; }
    public List<Assento> getAssentos() { return Collections.unmodifiableList(assentos); } // Protege a lista interna

    // Setters (controlados)
    public void setFilme(Filme filme) {
        this.filme = Objects.requireNonNull(filme, "Filme da sessão não pode ser nulo.");
    }

    public void setSala(Sala sala) {
        this.sala = Objects.requireNonNull(sala, "Sala da sessão não pode ser nula.");
        // Se a sala mudar, pode ser necessário regenerar/validar os assentos.
        // Por simplicidade, assumimos que a sala é definida na criação e não muda,
        // ou que a lógica de regeneração de assentos é tratada separadamente.
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        Objects.requireNonNull(dataHoraInicio, "Data e hora de início não podem ser nulos.");
        // Validações adicionais (ex: não no passado para novas sessões ativas,
        // conflito com outras sessões na mesma sala) seriam responsabilidade
        // de um Application Service ou Domain Service que tem acesso a mais contexto.
        this.dataHoraInicio = dataHoraInicio;
    }

    public void setPrecoIngressoBase(BigDecimal precoIngressoBase) {
        if (precoIngressoBase == null || precoIngressoBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço base do ingresso não pode ser nulo ou negativo.");
        }
        this.precoIngressoBase = precoIngressoBase;
    }

    // Métodos de Negócio

    /**
     * Adiciona um assento à sessão. Usado durante a configuração inicial da sessão.
     * Garante que o assento pertença a esta sessão.
     */
    public void adicionarAssento(Assento assento) {
        Objects.requireNonNull(assento, "Assento não pode ser nulo.");
        if (!assento.getSessao().getId().equals(this.id)) {
            throw new IllegalArgumentException("O assento não pertence a esta sessão.");
        }
        if (this.assentos.stream().anyMatch(a -> a.getIdentificador().equalsIgnoreCase(assento.getIdentificador()))) {
            throw new IllegalStateException("Assento com identificador '" + assento.getIdentificador() + "' já existe nesta sessão.");
        }
        this.assentos.add(assento);
    }

    public Optional<Assento> buscarAssentoPorIdentificador(String identificadorAssento) {
        if (identificadorAssento == null || identificadorAssento.trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador do assento não pode ser vazio.");
        }
        return this.assentos.stream()
                .filter(a -> a.getIdentificador().equalsIgnoreCase(identificadorAssento.trim()))
                .findFirst();
    }

    public Optional<Assento> buscarAssentoPorId(AssentoId assentoId) {
        Objects.requireNonNull(assentoId, "ID do assento não pode ser nulo.");
        return this.assentos.stream()
                .filter(a -> a.getId().equals(assentoId))
                .findFirst();
    }

    /**
     * Tenta reservar um assento temporariamente. (F1)
     * Lança IllegalStateException se a sessão não permite ou o assento não está disponível.
     */
    public void reservarAssentoTemporariamente(String identificadorAssento, ClienteId clienteId, int minutosParaExpirar) {
        if (this.status != StatusSessao.PROGRAMADA && this.status != StatusSessao.ABERTA) {
            throw new IllegalStateException("Sessão não está disponível para reserva de assentos (status: " + this.status + ").");
        }
        Assento assento = buscarAssentoPorIdentificador(identificadorAssento)
                .orElseThrow(() -> new IllegalArgumentException("Assento '" + identificadorAssento + "' não encontrado nesta sessão."));

        assento.reservarTemporariamente(clienteId, minutosParaExpirar); // Delega para a entidade Assento
        verificarEAtualizarStatusLotacao(); // F2
    }

    /**
     * Confirma a ocupação de um assento (após pagamento). (F1)
     */
    public void confirmarOcupacaoAssento(String identificadorAssento, ClienteId clienteIdQueReservou) {
        // clienteIdQueReservou pode ser usado para validar se quem reservou é quem está confirmando.
        if (this.status == StatusSessao.CANCELADA || this.status == StatusSessao.FINALIZADA) {
            throw new IllegalStateException("Não é possível confirmar assento em sessão com status: " + this.status);
        }
        Assento assento = buscarAssentoPorIdentificador(identificadorAssento)
                .orElseThrow(() -> new IllegalArgumentException("Assento '" + identificadorAssento + "' não encontrado nesta sessão."));

        // Validação opcional: verificar se clienteIdQueReservou corresponde ao clienteIdReservaTemporaria do assento
        if (assento.getClienteIdReservaTemporaria() != null && !assento.getClienteIdReservaTemporaria().equals(clienteIdQueReservou)) {
            // Isso pode ser uma RegraDeNegocioException, mas como o usuário pediu genéricas...
            throw new IllegalStateException("Tentativa de confirmar reserva de assento por cliente diferente do que reservou temporariamente.");
        }

        assento.confirmarOcupacao();
        verificarEAtualizarStatusLotacao(); // F2
    }

    /**
     * Libera um assento específico. (F1)
     */
    public void liberarAssentoEspecifico(String identificadorAssento) {
        Assento assento = buscarAssentoPorIdentificador(identificadorAssento)
                .orElseThrow(() -> new IllegalArgumentException("Assento '" + identificadorAssento + "' não encontrado nesta sessão."));
        assento.liberar();
        // Se a sessão estava lotada, seu status pode mudar.
        if (this.status == StatusSessao.LOTADA) {
            this.status = StatusSessao.ABERTA; // Ou PROGRAMADA, dependendo do estado anterior.
        }
    }

    /**
     * Libera todos os assentos que tinham reserva temporária expirada. (F1)
     * Este método seria chamado periodicamente por um Application Service.
     */
    public void processarExpiracaoReservasTemporarias(LocalDateTime agora) {
        boolean algumAssentoLiberado = false;
        for (Assento assento : this.assentos) {
            if (assento.liberarSeExpirado(agora)) {
                algumAssentoLiberado = true;
            }
        }
        if (algumAssentoLiberado && this.status == StatusSessao.LOTADA) {
            this.status = StatusSessao.ABERTA; // Pode não estar mais lotada
        }
    }

    /**
     * Verifica se a sessão está lotada e atualiza seu status. (F2)
     * Deve ser chamado após qualquer mudança no status de um assento.
     */
    public void verificarEAtualizarStatusLotacao() {
        if (this.status == StatusSessao.CANCELADA || this.status == StatusSessao.FINALIZADA || this.status == StatusSessao.EM_ANDAMENTO) {
            return; // Não muda status de lotação se já estiver nesses estados
        }

        boolean todosAssentosNaoBloqueadosOcupados = this.assentos.stream()
                .filter(a -> a.getStatus() != StatusAssento.BLOQUEADO)
                .allMatch(a -> a.getStatus() == StatusAssento.OCUPADO_FINAL);

        if (todosAssentosNaoBloqueadosOcupados) {
            if (this.status != StatusSessao.LOTADA) {
                this.status = StatusSessao.LOTADA;
                // Idealmente, aqui seria emitido um evento de domínio: SessaoLotadaEvent(this.id)
                // que seria tratado por um Observer (ex: para desabilitar botão de compra na UI).
                // Como estamos focando no modelo de domínio, essa notificação fica implícita.
                System.out.println("INFO DOMINIO: Sessão " + this.id + " marcada como LOTADA.");
            }
        } else {
            // Se não está tudo ocupado, mas estava LOTADA, volta para ABERTA (ou PROGRAMADA)
            if (this.status == StatusSessao.LOTADA) {
                // Determinar o status correto (ABERTA ou PROGRAMADA) pode depender se a venda já iniciou.
                // Simplificando, se não está lotada e as vendas estão ativas, fica ABERTA.
                this.status = StatusSessao.ABERTA; // Ou PROGRAMADA se as vendas não iniciaram.
                System.out.println("INFO DOMINIO: Sessão " + this.id + " não está mais LOTADA, status alterado para " + this.status);
            }
        }
    }

    /**
     * Cancela a sessão. (F4)
     * Dispara a necessidade de emitir créditos (lógica no Application Service).
     */
    public void cancelar() {
        if (this.status == StatusSessao.CANCELADA || this.status == StatusSessao.FINALIZADA) {
            throw new IllegalStateException("Sessão não pode ser cancelada pois seu status atual é: " + this.status);
        }
        this.status = StatusSessao.CANCELADA;
        // Libera todos os assentos que estavam reservados ou ocupados (a lógica exata de reembolso/crédito é externa)
        this.assentos.forEach(Assento::liberar); // Simplificado, pode precisar de mais nuance
        // Idealmente, emitir SessaoCanceladaEvent(this.id)
        System.out.println("INFO DOMINIO: Sessão " + this.id + " CANCELADA.");
    }

    public boolean permiteNovasCompras() { // Para F2
        return (this.status == StatusSessao.PROGRAMADA || this.status == StatusSessao.ABERTA) &&
                LocalDateTime.now().isBefore(this.dataHoraInicio); // Não permite compra se já começou
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
                ", totalAssentos=" + assentos.size() +
                '}';
    }
}
