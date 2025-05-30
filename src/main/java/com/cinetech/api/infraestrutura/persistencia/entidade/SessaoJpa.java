package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.enums.TipoExibicao;
import jakarta.persistence.*; // JPA de Jakarta EE / Spring Boot 3+
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sessao")
public class SessaoJpa {

    @Id
    private UUID id; // Chave primária

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filme_id", nullable = false)
    private FilmeJpa filme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaJpa sala;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoExibicao tipoExibicao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoIngressoBase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusSessao status;

    // Uma sessão tem muitos assentos.
    // CascadeType.ALL e orphanRemoval=true significam que os AssentoJpa
    // são gerenciados pelo ciclo de vida da SessaoJpa.
    @OneToMany(mappedBy = "sessao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AssentoJpa> assentos = new ArrayList<>();

    // Construtor padrão exigido pelo JPA
    public SessaoJpa() {
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public FilmeJpa getFilme() {
        return filme;
    }

    public void setFilme(FilmeJpa filme) {
        this.filme = filme;
    }

    public SalaJpa getSala() {
        return sala;
    }

    public void setSala(SalaJpa sala) {
        this.sala = sala;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }

    public BigDecimal getPrecoIngressoBase() {
        return precoIngressoBase;
    }

    public void setPrecoIngressoBase(BigDecimal precoIngressoBase) {
        this.precoIngressoBase = precoIngressoBase;
    }

    public StatusSessao getStatus() {
        return status;
    }

    public void setStatus(StatusSessao status) {
        this.status = status;
    }

    public List<AssentoJpa> getAssentos() {
        return assentos;
    }

    public void setAssentos(List<AssentoJpa> assentos) {
        this.assentos = assentos;
    }

    // Método utilitário para adicionar assentos e manter o relacionamento bidirecional
    public void adicionarAssentoJpa(AssentoJpa assento) {
        this.assentos.add(assento);
        assento.setSessao(this);
    }

    public void removerAssentoJpa(AssentoJpa assento) {
        this.assentos.remove(assento);
        assento.setSessao(null);
    }
}