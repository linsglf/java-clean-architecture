package com.cinetech.api.dominio.modelos.assento;

import com.cinetech.api.dominio.enums.StatusAssento;
import com.cinetech.api.dominio.enums.TipoAssento;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.sessao.Sessao;

import java.time.LocalDateTime;
import java.util.Objects;

public class Assento {
    private final AssentoId id;
    private final Sessao sessao; // Referência à raiz do agregado. Assento não existe sem Sessao.
    private final String identificadorPosicao; // Ex: "A1", "C5" (imutável após criação)
    private final TipoAssento tipo; // comum, VIP, PCD [cite: 4]
    private StatusAssento status;
    private ClienteId clienteIdReservaTemporaria;
    private LocalDateTime timestampExpiracaoReserva;

    // Construtor usado pela Sessao ao criar seus assentos
    public Assento(Sessao sessao, String identificadorPosicao, TipoAssento tipo) {
        this(AssentoId.novo(), sessao, identificadorPosicao, tipo, StatusAssento.DISPONIVEL, null, null);
    }

    // Construtor completo para reconstituição
    public Assento(AssentoId id, Sessao sessao, String identificadorPosicao, TipoAssento tipo, StatusAssento status,
                   ClienteId clienteIdReservaTemporaria, LocalDateTime timestampExpiracaoReserva) {
        this.id = Objects.requireNonNull(id, "ID do Assento não pode ser nulo.");
        this.sessao = Objects.requireNonNull(sessao, "Sessão do assento não pode ser nula.");
        if (identificadorPosicao == null || identificadorPosicao.trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador de posição do assento não pode ser vazio.");
        }
        this.identificadorPosicao = identificadorPosicao.trim();
        this.tipo = Objects.requireNonNull(tipo, "Tipo do assento não pode ser nulo.");
        this.status = Objects.requireNonNull(status, "Status do assento não pode ser nulo.");
        this.clienteIdReservaTemporaria = clienteIdReservaTemporaria;
        this.timestampExpiracaoReserva = timestampExpiracaoReserva;
    }

    // Getters
    public AssentoId getId() { return id; }
    public Sessao getSessao() { return sessao; }
    public String getIdentificadorPosicao() { return identificadorPosicao; }
    public TipoAssento getTipo() { return tipo; }
    public StatusAssento getStatus() { return status; }
    public ClienteId getClienteIdReservaTemporaria() { return clienteIdReservaTemporaria; }
    public LocalDateTime getTimestampExpiracaoReserva() { return timestampExpiracaoReserva; }

    // Métodos de Negócio

    private boolean estaDisponivelParaReservaLogica(LocalDateTime agora) {
        // Um assento BLOQUEADO nunca está disponível para reserva por cliente
        if (this.status == StatusAssento.BLOQUEADO) return false;

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
     * A regra de "lugares especiais estejam sempre disponíveis para quem precisa" (F14)
     * pode ser parcialmente implementada aqui ou em um Application Service.
     * Ex: Se tipo == PCD, talvez só cliente com perfil PCD possa reservar, ou tem prioridade.
     * Por ora, a lógica de perfil será tratada no Application Service.
     */
    public void reservarTemporariamente(ClienteId clienteId, int minutosParaExpirar) {
        Objects.requireNonNull(clienteId, "ID do cliente não pode ser nulo para reserva temporária.");
        if (minutosParaExpirar <= 0) {
            throw new IllegalArgumentException("Tempo de expiração da reserva deve ser positivo.");
        }

        if (!estaDisponivelParaReservaLogica(LocalDateTime.now())) {
            throw new IllegalStateException("Assento '" + identificadorPosicao + "' na sessão " + sessao.getId() +
                    " não está disponível para reserva (status atual: " + this.status + ").");
        }

        // Regra F14 (parcial): "lugares especiais estejam sempre disponíveis para quem precisa"
        // Uma forma simples seria: se o assento é PCD e o cliente não tem perfil PCD,
        // a reserva poderia ser negada ou ter condições especiais.
        // Esta lógica é mais complexa e provavelmente melhor gerenciada no Application Service
        // que tem acesso ao perfil do Cliente. A entidade Assento só sabe seu tipo.

        this.status = StatusAssento.RESERVADO_TEMP;
        this.clienteIdReservaTemporaria = clienteId;
        this.timestampExpiracaoReserva = LocalDateTime.now().plusMinutes(minutosParaExpirar);
    }

    /**
     * Confirma a ocupação do assento. (F1)
     * Geralmente chamado após o pagamento ser confirmado.
     */
    public void confirmarOcupacaoDefinitiva() {
        if (this.status == StatusAssento.OCUPADO_FINAL) {
            // Já está no estado desejado. Poderia ser um log ou um retorno silencioso.
            System.out.println("WARN DOMINIO: Assento " + identificadorPosicao + " da sessão " + sessao.getId() + " já está OCUPADO_FINAL.");
            return;
        }
        if (this.status == StatusAssento.BLOQUEADO) {
            throw new IllegalStateException("Assento '" + identificadorPosicao + "' está BLOQUEADO e não pode ser ocupado.");
        }
        // A validação se o clienteIdReservaTemporaria corresponde ao cliente que pagou
        // é responsabilidade do Application Service, que tem ambos os contextos.
        this.status = StatusAssento.OCUPADO_FINAL;
        this.clienteIdReservaTemporaria = null; // Limpa dados da reserva temporária
        this.timestampExpiracaoReserva = null;
    }

    /**
     * Libera o assento, tornando-o disponível. (F1)
     * Usado quando uma reserva temporária expira ou uma compra/reserva é cancelada.
     */
    public void liberar() {
        // Não se libera um assento BLOQUEADO por esta operação de cliente/sistema.
        // Apenas assentos RESERVADO_TEMP ou OCUPADO_FINAL (em caso de cancelamento de ingresso com reembolso)
        // podem voltar a ser DISPONIVEL.
        if (this.status == StatusAssento.RESERVADO_TEMP || this.status == StatusAssento.OCUPADO_FINAL) {
            this.status = StatusAssento.DISPONIVEL;
            this.clienteIdReservaTemporaria = null;
            this.timestampExpiracaoReserva = null;
        } else if (this.status == StatusAssento.DISPONIVEL) {
            // Já está disponível, não faz nada.
        } else if (this.status == StatusAssento.BLOQUEADO) {
            // Não deve ser liberado por esta operação.
            throw new IllegalStateException("Assento '" + identificadorPosicao + "' está BLOQUEADO e não pode ser liberado por esta operação.");
        }
    }

    /**
     * Libera o assento se sua reserva temporária expirou. (F1)
     * Retorna true se foi liberado, false caso contrário.
     */
    public boolean liberarSeReservaTemporariaExpirada(LocalDateTime agora) {
        Objects.requireNonNull(agora, "Data de referência para expiração não pode ser nula.");
        if (this.status == StatusAssento.RESERVADO_TEMP && estaReservaTemporariaExpirada(agora)) {
            liberar();
            return true;
        }
        return false;
    }

    /**
     * Bloqueia o assento por motivos administrativos.
     */
    public void bloquearAdministrativamente() {
        // Adicionar regras se necessário (ex: não pode bloquear se estiver OCUPADO_FINAL com ingresso vendido e sessão próxima?)
        this.status = StatusAssento.BLOQUEADO;
        this.clienteIdReservaTemporaria = null;
        this.timestampExpiracaoReserva = null;
    }

    /**
     * Desbloqueia um assento previamente bloqueado, tornando-o disponível.
     */
    public void desbloquearAdministrativamente() {
        if (this.status != StatusAssento.BLOQUEADO) {
            throw new IllegalStateException("Assento '" + identificadorPosicao + "' não pode ser desbloqueado pois não está BLOQUEADO. Status atual: " + this.status);
        }
        liberar(); // Torna disponível
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
                ", identificadorPosicao='" + identificadorPosicao + '\'' +
                ", tipo=" + tipo +
                ", status=" + status +
                ", sessaoId=" + (sessao != null ? sessao.getId() : "N/A") +
                '}';
    }
}
