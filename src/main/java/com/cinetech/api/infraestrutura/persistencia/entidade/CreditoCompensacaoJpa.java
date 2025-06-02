package com.cinetech.api.infraestrutura.persistencia.entidade;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "credito_compensacao")
public class CreditoCompensacaoJpa {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpa cliente; // Correto: armazena o objeto ClienteJpa

    // ... outros campos como antes ...
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorOriginal;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUtilizado;
    @Column(nullable = false)
    private LocalDateTime dataEmissao;
    @Column(nullable = true)
    private LocalDateTime dataValidade;
    @Column(nullable = false)
    private boolean ativo;
    @Column(nullable = false, length = 255)
    private String motivo;
    @Column(name = "sessao_origem_id")
    private UUID sessaoOrigemId;


    public CreditoCompensacaoJpa() {
    }

    // Getters e Setters (para todos os campos, incluindo 'cliente')
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ClienteJpa getCliente() { return cliente; }
    public void setCliente(ClienteJpa cliente) { this.cliente = cliente; }
    public BigDecimal getValorOriginal() { return valorOriginal; }
    public void setValorOriginal(BigDecimal valorOriginal) { this.valorOriginal = valorOriginal; }
    public BigDecimal getValorUtilizado() { return valorUtilizado; }
    public void setValorUtilizado(BigDecimal valorUtilizado) { this.valorUtilizado = valorUtilizado; }
    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }
    public LocalDateTime getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDateTime dataValidade) { this.dataValidade = dataValidade; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public UUID getSessaoOrigemId() { return sessaoOrigemId; }
    public void setSessaoOrigemId(UUID sessaoOrigemId) { this.sessaoOrigemId = sessaoOrigemId; }


    // O método getClienteId() que você tinha aqui é desnecessário se o mapper cuidar da conversão
    // public UUID getClienteId() {
    //     return cliente != null ? cliente.getId() : null;
    // }


    @Override
    public boolean equals(Object o) { /* ... como antes ... */
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditoCompensacaoJpa that = (CreditoCompensacaoJpa) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { /* ... como antes ... */
        return Objects.hash(id);
    }
}