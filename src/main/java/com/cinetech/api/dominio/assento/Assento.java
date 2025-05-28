package com.cinetech.api.dominio.assento;

import com.cinetech.api.dominio.cliente.ClienteId;
import com.cinetech.api.dominio.enums.StatusAssento;
import com.cinetech.api.dominio.sessao.Sessao;

import java.time.LocalDateTime;
import java.util.Objects;

public class Assento {
    private final AssentoId id;
    private final Sessao sessao; // Referência à raiz do agregado. Imutável após criação.
    private final String identificador; // Ex: "A1", "C5". Imutável após criação.
    private StatusAssento status;
    private ClienteId clienteIdReservaTemporaria; // Cliente que fez a reserva temporária
    private LocalDateTime timestampExpiracaoReserva;

    // Construtor principal usado pela Sessao ao criar seus assentos.
    public Assento(Sessao sessao, String identificador) {
        this(AssentoId.novo(), sessao, identificador, StatusAssento.DISPONIVEL, null, null);
    }

    // Construtor para reconstituição (usado pela persistência/mappers)
    public Assento(AssentoId id, Sessao sessao, String identificador, StatusAssento status,
                   ClienteId clienteIdReservaTemporaria, LocalDateTime timestampExpiracaoReserva) {
        this.id = Objects.requireNonNull(id, "ID do Assento não pode ser nulo.");
        this.sessao = Objects.requireNonNull(sessao, "Sessão do assento não pode ser nula."); // Assento sempre pertence a uma sessão
        if (identificador == null || identificador.trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador do assento não pode ser vazio.");
        }
        this.identificador = identificador.trim();
        this.status = Objects.requireNonNull(status, "Status do assento não pode ser nulo.");
        this.clienteIdReservaTemporaria = clienteIdReservaTemporaria; // Pode ser nulo
        this.timestampExpiracaoReserva = timestampExpiracaoReserva;   // Pode ser nulo
    }

    // Getters
    public AssentoId getId() { return id; }
    public Sessao getSessao() { return sessao; }
    public String getIdentificador() { return identificador; }
    public StatusAssento getStatus() { return status; }
    public ClienteId getClienteIdReservaTemporaria() { return clienteIdReservaTemporaria; }
    public LocalDateTime getTimestampExpiracaoReserva() { return timestampExpiracaoReserva; }

    // Métodos de Negócio

    private boolean estaDisponivelParaReservaTemporaria(LocalDateTime agora) {
        return this.status == StatusAssento.DISPONIVEL ||
                (this.status == StatusAssento.RESERVADO_TEMP && estaReservaTemporariaExpirada(agora));
    }

    private boolean estaReservaTemporariaExpirada(LocalDateTime agora) {
        Objects.requireNonNull(agora, "Data de referência para expiração não pode ser nula.");
        return this.timestampExpiracaoReserva != null && agora.isAfter(this.timestampExpiracaoReserva);
    }

    /**
     * Tenta reservar este assento temporariamente para um cliente. (F1)
     * Lança IllegalStateException se não puder ser reservado.
     */
    public void reservarTemporariamente(ClienteId clienteId, int minutosParaExpirar) {
        Objects.requireNonNull(clienteId, "ID do cliente não pode ser nulo para reserva temporária.");
        if (minutosParaExpirar <= 0) {
            throw new IllegalArgumentException("Tempo de expiração da reserva deve ser positivo.");
        }

        if (!estaDisponivelParaReservaTemporaria(LocalDateTime.now())) {
            throw new IllegalStateException("Assento '" + identificador + "' na sessão " + sessao.getId() +
                    " não está disponível para reserva (status atual: " + this.status + ").");
        }

        this.status = StatusAssento.RESERVADO_TEMP;
        this.clienteIdReservaTemporaria = clienteId;
        this.timestampExpiracaoReserva = LocalDateTime.now().plusMinutes(minutosParaExpirar);
    }

    /**
     * Confirma a ocupação do assento. (F1)
     * Geralmente chamado após o pagamento ser confirmado.
     */
    public void confirmarOcupacao() {
        // Validação se pode ser confirmado (ex: estava RESERVADO_TEMP ou DISPONIVEL em alguns fluxos)
        if (this.status == StatusAssento.OCUPADO_FINAL) {
            System.out.println("WARN DOMINIO: Assento " + identificador + " já está OCUPADO_FINAL.");
            return; // Já está no estado desejado, ou erro de fluxo.
        }
        if (this.status == StatusAssento.BLOQUEADO) {
            throw new IllegalStateException("Assento '" + identificador + "' está bloqueado e não pode ser ocupado.");
        }
        // A validação se o clienteIdReservaTemporaria corresponde ao cliente que pagou
        // pode ser feita no Application Service antes de chamar este método.
        this.status = StatusAssento.OCUPADO_FINAL;
        this.clienteIdReservaTemporaria = null; // Limpa dados da reserva temporária
        this.timestampExpiracaoReserva = null;
    }

    /**
     * Libera o assento, tornando-o disponível. (F1)
     * Usado quando uma reserva temporária expira ou uma compra é cancelada antes da confirmação.
     */
    public void liberar() {
        this.status = StatusAssento.DISPONIVEL;
        this.clienteIdReservaTemporaria = null;
        this.timestampExpiracaoReserva = null;
    }

    /**
     * Libera o assento se sua reserva temporária expirou. (F1)
     * Retorna true se foi liberado, false caso contrário.
     */
    public boolean liberarSeExpirado(LocalDateTime agora) {
        if (this.status == StatusAssento.RESERVADO_TEMP && estaReservaTemporariaExpirada(agora)) {
            liberar();
            return true;
        }
        return false;
    }

    /**
     * Bloqueia o assento por motivos administrativos.
     */
    public void bloquear() {
        // Adicionar regras se necessário (ex: não pode bloquear se estiver OCUPADO_FINAL com ingresso vendido)
        this.status = StatusAssento.BLOQUEADO;
        this.clienteIdReservaTemporaria = null;
        this.timestampExpiracaoReserva = null;
    }

    /**
     * Desbloqueia um assento previamente bloqueado, tornando-o disponível.
     */
    public void desbloquear() {
        if (this.status != StatusAssento.BLOQUEADO) {
            throw new IllegalStateException("Assento '" + identificador + "' não pode ser desbloqueado pois não está BLOQUEADO.");
        }
        liberar(); // Reutiliza a lógica de liberar para o estado DISPONIVEL
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assento assento = (Assento) o;
        return id.equals(assento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Assento{" +
                "id=" + id +
                ", identificador='" + identificador + '\'' +
                ", status=" + status +
                ", sessaoId=" + (sessao != null ? sessao.getId() : "N/A") +
                '}';
    }
}
