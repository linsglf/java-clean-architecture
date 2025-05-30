package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.PerfilCliente;
import com.cinetech.api.dominio.enums.TipoPromocao;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "promocao")
public class PromocaoJpa {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nomeDescritivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoPromocao tipoPromocao;

    @Column(precision = 5, scale = 4) // Ex: 0.5000 para 50%
    private BigDecimal percentualDesconto;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorDescontoFixo;

    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.EAGER) // EAGER pode ser ok para poucos elementos
    @CollectionTable(name = "promocao_dias_semana", joinColumns = @JoinColumn(name = "promocao_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private Set<DayOfWeek> diasDaSemanaAplicaveis = new HashSet<>();

    private LocalTime horarioInicioAplicavel;
    private LocalTime horarioFimAplicavel;

    @ElementCollection(targetClass = PerfilCliente.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "promocao_perfis_cliente", joinColumns = @JoinColumn(name = "promocao_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_cliente", nullable = false)
    private Set<PerfilCliente> perfisAplicaveis = new HashSet<>();

    @Column(nullable = false)
    private boolean ativa;

    private LocalDate dataInicioVigencia;
    private LocalDate dataFimVigencia;

    public PromocaoJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNomeDescritivo() { return nomeDescritivo; }
    public void setNomeDescritivo(String nomeDescritivo) { this.nomeDescritivo = nomeDescritivo; }
    public TipoPromocao getTipoPromocao() { return tipoPromocao; }
    public void setTipoPromocao(TipoPromocao tipoPromocao) { this.tipoPromocao = tipoPromocao; }
    public BigDecimal getPercentualDesconto() { return percentualDesconto; }
    public void setPercentualDesconto(BigDecimal percentualDesconto) { this.percentualDesconto = percentualDesconto; }
    public BigDecimal getValorDescontoFixo() { return valorDescontoFixo; }
    public void setValorDescontoFixo(BigDecimal valorDescontoFixo) { this.valorDescontoFixo = valorDescontoFixo; }
    public Set<DayOfWeek> getDiasDaSemanaAplicaveis() { return diasDaSemanaAplicaveis; }
    public void setDiasDaSemanaAplicaveis(Set<DayOfWeek> diasDaSemanaAplicaveis) { this.diasDaSemanaAplicaveis = diasDaSemanaAplicaveis; }
    public LocalTime getHorarioInicioAplicavel() { return horarioInicioAplicavel; }
    public void setHorarioInicioAplicavel(LocalTime horarioInicioAplicavel) { this.horarioInicioAplicavel = horarioInicioAplicavel; }
    public LocalTime getHorarioFimAplicavel() { return horarioFimAplicavel; }
    public void setHorarioFimAplicavel(LocalTime horarioFimAplicavel) { this.horarioFimAplicavel = horarioFimAplicavel; }
    public Set<PerfilCliente> getPerfisAplicaveis() { return perfisAplicaveis; }
    public void setPerfisAplicaveis(Set<PerfilCliente> perfisAplicaveis) { this.perfisAplicaveis = perfisAplicaveis; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public LocalDate getDataInicioVigencia() { return dataInicioVigencia; }
    public void setDataInicioVigencia(LocalDate dataInicioVigencia) { this.dataInicioVigencia = dataInicioVigencia; }
    public LocalDate getDataFimVigencia() { return dataFimVigencia; }
    public void setDataFimVigencia(LocalDate dataFimVigencia) { this.dataFimVigencia = dataFimVigencia; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromocaoJpa that = (PromocaoJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
