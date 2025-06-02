package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.StatusAvaliacao; // Ajuste o pacote se necessário
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "avaliacao", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"filme_id", "cliente_id"}) // Garante que um cliente avalia um filme apenas uma vez
})
public class AvaliacaoJpa {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filme_id", nullable = false) // Referencia a coluna de FK
    private FilmeJpa filme; // <<< ARMAZENA O OBJETO FilmeJpa

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false) // Referencia a coluna de FK
    private ClienteJpa cliente; // <<< ARMAZENA O OBJETO ClienteJpa

    @Column(nullable = false)
    private int nota;

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

    // Getters e Setters para todos os campos
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public FilmeJpa getFilme() { return filme; } // <<< Getter para FilmeJpa
    public void setFilme(FilmeJpa filme) { this.filme = filme; } // <<< Setter para FilmeJpa

    public ClienteJpa getCliente() { return cliente; } // <<< Getter para ClienteJpa
    public void setCliente(ClienteJpa cliente) { this.cliente = cliente; } // <<< Setter para ClienteJpa

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