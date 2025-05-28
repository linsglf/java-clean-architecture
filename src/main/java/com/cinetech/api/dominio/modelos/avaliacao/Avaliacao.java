package com.cinetech.api.dominio.modelos.avaliacao;

import com.cinetech.api.dominio.enums.StatusAvaliacao;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.filme.FilmeId;

import java.time.LocalDateTime;
import java.util.Objects;

public class Avaliacao {
    private final AvaliacaoId id;
    private final FilmeId filmeId;
    private final ClienteId clienteId; // Cliente que fez a avaliação
    private int nota; // Ex: 1 a 5 estrelas
    private String comentario; // Pode ser opcional
    private final LocalDateTime dataAvaliacao;
    private StatusAvaliacao statusVisibilidade; // Para F8 (moderação) [cite: 21]

    // Construtor para nova avaliação
    public Avaliacao(FilmeId filmeId, ClienteId clienteId, int nota, String comentario) {
        this(AvaliacaoId.novo(), filmeId, clienteId, nota, comentario, LocalDateTime.now(),
                StatusAvaliacao.PENDENTE_MODERACAO); // Nova avaliação começa pendente
    }

    // Construtor completo para reconstituição
    public Avaliacao(AvaliacaoId id, FilmeId filmeId, ClienteId clienteId, int nota, String comentario,
                     LocalDateTime dataAvaliacao, StatusAvaliacao statusVisibilidade) {
        this.id = Objects.requireNonNull(id, "ID da Avaliação não pode ser nulo.");
        this.filmeId = Objects.requireNonNull(filmeId, "ID do Filme não pode ser nulo para avaliação.");
        this.clienteId = Objects.requireNonNull(clienteId, "ID do Cliente não pode ser nulo para avaliação.");
        setNota(nota);
        setComentario(comentario);
        this.dataAvaliacao = Objects.requireNonNull(dataAvaliacao, "Data da avaliação não pode ser nula.");
        this.statusVisibilidade = Objects.requireNonNull(statusVisibilidade, "Status de visibilidade da avaliação não pode ser nulo.");
    }

    // Getters
    public AvaliacaoId getId() { return id; }
    public FilmeId getFilmeId() { return filmeId; }
    public ClienteId getClienteId() { return clienteId; }
    public int getNota() { return nota; }
    public String getComentario() { return comentario; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public StatusAvaliacao getStatusVisibilidade() { return statusVisibilidade; }

    // Setters (controlados)
    public void setNota(int nota) {
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("Nota da avaliação deve ser entre 1 e 5. Recebido: " + nota);
        }
        this.nota = nota;
    }

    public void setComentario(String comentario) {
        // Validações de tamanho máximo podem ser adicionadas
        if (comentario != null && comentario.length() > 1000) { // Exemplo de limite
            throw new IllegalArgumentException("Comentário excede o limite de 1000 caracteres.");
        }
        this.comentario = comentario; // Comentário pode ser nulo ou vazio se permitido
    }

    // Métodos de Negócio para F8
    public void aprovarVisibilidade() {
        if (this.statusVisibilidade != StatusAvaliacao.PENDENTE_MODERACAO) {
            throw new IllegalStateException("Avaliação com ID " + this.id + " não pode ser aprovada pois seu status é " + this.statusVisibilidade);
        }
        this.statusVisibilidade = StatusAvaliacao.APROVADA;
    }

    public void marcarComoConteudoOfensivo() { // [cite: 21]
        // Pode ser chamada após análise externa (manual ou por um Domain Service de filtro)
        this.statusVisibilidade = StatusAvaliacao.REPROVADA_OFENSIVA;
    }

    public void ocultarPeloAutor() {
        // Permite ao próprio usuário que fez a avaliação ocultá-la (se visível)
        if (this.statusVisibilidade == StatusAvaliacao.APROVADA) {
            this.statusVisibilidade = StatusAvaliacao.OCULTA_PELO_AUTOR;
        } else {
            // Não se pode ocultar o que não está aprovado ou já está oculto de outra forma
            throw new IllegalStateException("Avaliação com ID " + this.id + " não pode ser ocultada pelo autor pois seu status é " + this.statusVisibilidade);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return id.equals(avaliacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Avaliacao{" +
                "id=" + id +
                ", filmeId=" + filmeId +
                ", clienteId=" + clienteId +
                ", nota=" + nota +
                ", statusVisibilidade=" + statusVisibilidade +
                '}';
    }
}
