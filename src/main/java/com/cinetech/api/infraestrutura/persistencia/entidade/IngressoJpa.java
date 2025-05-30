package com.cinetech.api.infraestrutura.persistencia.entidade;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ingresso")
public class IngressoJpa {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpa cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoJpa sessao;

    // Um Ingresso está ligado a um Assento específico daquela Sessao.
    // O relacionamento é OneToOne porque um assento específico em uma sessão só pode ter um ingresso vendido.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assento_id", nullable = false, unique = true)
    private AssentoJpa assento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;

    @Column(nullable = false)
    private LocalDateTime dataCompra;

    @Column(nullable = false)
    private boolean meiaEntradaAplicada;

    @Column(name = "promocao_id") // Armazena o UUID da PromocaoId
    private UUID promocaoAplicadaId; // Pode ser nulo se nenhuma promoção específica foi aplicada

    @Column(nullable = false, unique = true, length = 50)
    private String codigoValidacao;

    @Column(nullable = false)
    private boolean validadoNaEntrada;

    public IngressoJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ClienteJpa getCliente() { return cliente; }
    public void setCliente(ClienteJpa cliente) { this.cliente = cliente; }
    public SessaoJpa getSessao() { return sessao; }
    public void setSessao(SessaoJpa sessao) { this.sessao = sessao; }
    public AssentoJpa getAssento() { return assento; }
    public void setAssento(AssentoJpa assento) { this.assento = assento; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
    public boolean isMeiaEntradaAplicada() { return meiaEntradaAplicada; }
    public void setMeiaEntradaAplicada(boolean meiaEntradaAplicada) { this.meiaEntradaAplicada = meiaEntradaAplicada; }
    public UUID getPromocaoAplicadaId() { return promocaoAplicadaId; }
    public void setPromocaoAplicadaId(UUID promocaoAplicadaId) { this.promocaoAplicadaId = promocaoAplicadaId; }
    public String getCodigoValidacao() { return codigoValidacao; }
    public void setCodigoValidacao(String codigoValidacao) { this.codigoValidacao = codigoValidacao; }
    public boolean isValidadoNaEntrada() { return validadoNaEntrada; }
    public void setValidadoNaEntrada(boolean validadoNaEntrada) { this.validadoNaEntrada = validadoNaEntrada; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngressoJpa that = (IngressoJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
