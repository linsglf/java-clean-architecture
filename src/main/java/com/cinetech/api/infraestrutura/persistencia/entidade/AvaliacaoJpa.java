package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.StatusAvaliacao;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "avaliacao", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"filme_id", "cliente_id"}) // Cliente só pode avaliar um filme uma vez
})
public class AvaliacaoJpa {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filme_id", nullable = false)
    private FilmeJpa filme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpa cliente;

    @Column(nullable = false)
    private int nota; // 1 a 5

    @Lob
    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime dataAvaliacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusAvaliacao statusVisibilidade;

    public AvaliacaoJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public FilmeJpa getFilme() { return filme; }
    public void setFilme(FilmeJpa filme) { this.filme = filme; }
    public ClienteJpa getCliente() { return cliente; }
    public void setCliente(ClienteJpa cliente) { this.cliente = cliente; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
    public StatusAvaliacao getStatusVisibilidade() { return statusVisibilidade; }
    public void setStatusVisibilidade(StatusAvaliacao statusVisibilidade) { this.statusVisibilidade = statusVisibilidade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvaliacaoJpa that = (AvaliacaoJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
