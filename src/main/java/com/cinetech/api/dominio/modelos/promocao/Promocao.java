package com.cinetech.api.dominio.modelos.promocao;

import com.cinetech.api.dominio.enums.PerfilCliente;
import com.cinetech.api.dominio.enums.TipoPromocao;
import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.sessao.Sessao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set; // Para dias da semana ou perfis

public class Promocao {
    private final PromocaoId id;
    private String nomeDescritivo;
    private TipoPromocao tipoPromocao;
    private BigDecimal percentualDesconto;
    private BigDecimal valorDescontoFixo;

    // Campos para promoções de horário
    private Set<DayOfWeek> diasDaSemanaAplicaveis;
    private LocalTime horarioInicioAplicavel;
    private LocalTime horarioFimAplicavel;

    // Campo para promoções de perfil
    private Set<PerfilCliente> perfisAplicaveis;

    private boolean ativa;
    private LocalDate dataInicioVigencia; // Nova adição
    private LocalDate dataFimVigencia;    // Nova adição

    // Construtor para desconto de perfil (ex: meia-entrada)
    public Promocao(String nomeDescritivo, TipoPromocao tipoPromocao, BigDecimal percentualDesconto,
                    Set<PerfilCliente> perfisAplicaveis, boolean ativa,
                    LocalDate dataInicioVigencia, LocalDate dataFimVigencia) {
        this(PromocaoId.novo(), nomeDescritivo, tipoPromocao, percentualDesconto, null,
                null, null, null, perfisAplicaveis, ativa,
                dataInicioVigencia, dataFimVigencia);
        if (tipoPromocao != TipoPromocao.MEIA_ENTRADA_PERFIL) { // Validação específica do construtor
            throw new IllegalArgumentException("Este construtor é para promoções do tipo MEIA_ENTRADA_PERFIL.");
        }
    }

    // Construtor para desconto de horário
    public Promocao(String nomeDescritivo, TipoPromocao tipoPromocao, BigDecimal valorDescontoFixo, // Pode ser percentual também
                    Set<DayOfWeek> diasDaSemanaAplicaveis, LocalTime horarioInicio, LocalTime horarioFim,
                    boolean ativa, LocalDate dataInicioVigencia, LocalDate dataFimVigencia) {
        this(PromocaoId.novo(), nomeDescritivo, tipoPromocao, null, valorDescontoFixo, // Exemplo com valor fixo
                diasDaSemanaAplicaveis, horarioInicio, horarioFim, null, ativa,
                dataInicioVigencia, dataFimVigencia);
        if (tipoPromocao != TipoPromocao.HORARIO_BAIXA_DEMANDA) { // Validação específica
            throw new IllegalArgumentException("Este construtor é para promoções do tipo HORARIO_BAIXA_DEMANDA.");
        }
    }

    // Construtor principal completo para criação/reconstituição
    public Promocao(PromocaoId id, String nomeDescritivo, TipoPromocao tipoPromocao,
                    BigDecimal percentualDesconto, BigDecimal valorDescontoFixo,
                    Set<DayOfWeek> diasDaSemanaAplicaveis, LocalTime horarioInicioAplicavel, LocalTime horarioFimAplicavel,
                    Set<PerfilCliente> perfisAplicaveis, boolean ativa,
                    LocalDate dataInicioVigencia, LocalDate dataFimVigencia) {
        this.id = Objects.requireNonNull(id, "ID da Promoção não pode ser nulo.");
        setNomeDescritivo(nomeDescritivo);
        this.tipoPromocao = Objects.requireNonNull(tipoPromocao, "Tipo da promoção não pode ser nulo.");

        if ((percentualDesconto != null && valorDescontoFixo != null) || (percentualDesconto == null && valorDescontoFixo == null)) {
            throw new IllegalArgumentException("Deve ser fornecido ou percentualDeDesconto ou valorDescontoFixo para a promoção, mas não ambos ou nenhum.");
        }
        if (percentualDesconto != null && (percentualDesconto.compareTo(BigDecimal.ZERO) <= 0 || percentualDesconto.compareTo(BigDecimal.ONE) > 0)) {
            throw new IllegalArgumentException("Percentual de desconto deve ser maior que 0 e menor ou igual a 1 (0 a 100%). Recebido: " + percentualDesconto);
        }
        if (valorDescontoFixo != null && valorDescontoFixo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de desconto fixo deve ser maior que zero. Recebido: " + valorDescontoFixo);
        }
        this.percentualDesconto = percentualDesconto;
        this.valorDescontoFixo = valorDescontoFixo;

        this.diasDaSemanaAplicaveis = diasDaSemanaAplicaveis == null ? Set.of() : Set.copyOf(diasDaSemanaAplicaveis);
        this.horarioInicioAplicavel = horarioInicioAplicavel;
        this.horarioFimAplicavel = horarioFimAplicavel;
        if (this.horarioInicioAplicavel != null && this.horarioFimAplicavel != null && this.horarioFimAplicavel.isBefore(this.horarioInicioAplicavel)){
            throw new IllegalStateException("Horário final da promoção não pode ser anterior ao horário inicial.");
        }

        this.perfisAplicaveis = perfisAplicaveis == null ? Set.of() : Set.copyOf(perfisAplicaveis);
        this.ativa = ativa;
        this.dataInicioVigencia = dataInicioVigencia; // Pode ser nulo se sempre vigente
        this.dataFimVigencia = dataFimVigencia;     // Pode ser nulo se sempre vigente
        validarConsistenciaDatasVigencia();
    }

    // Getters
    public PromocaoId getId() { return id; }
    public String getNomeDescritivo() { return nomeDescritivo; }
    public TipoPromocao getTipoPromocao() { return tipoPromocao; }
    public BigDecimal getPercentualDesconto() { return percentualDesconto; }
    public BigDecimal getValorDescontoFixo() { return valorDescontoFixo; }
    public Set<DayOfWeek> getDiasDaSemanaAplicaveis() { return diasDaSemanaAplicaveis; }
    public LocalTime getHorarioInicioAplicavel() { return horarioInicioAplicavel; }
    public LocalTime getHorarioFimAplicavel() { return horarioFimAplicavel; }
    public Set<PerfilCliente> getPerfisAplicaveis() { return perfisAplicaveis; }
    public boolean isAtiva() { return ativa; } // Mantemos para o controle explícito on/off
    public LocalDate getDataInicioVigencia() { return dataInicioVigencia; }
    public LocalDate getDataFimVigencia() { return dataFimVigencia; }


    // Setters (para campos que podem ser alterados após a criação)
    public void setNomeDescritivo(String nomeDescritivo) {
        if (nomeDescritivo == null || nomeDescritivo.trim().isEmpty()){
            throw new IllegalArgumentException("Nome descritivo da promoção não pode ser vazio.");
        }
        this.nomeDescritivo = nomeDescritivo.trim();
    }

    public void setDataInicioVigencia(LocalDate dataInicioVigencia) {
        this.dataInicioVigencia = dataInicioVigencia;
        validarConsistenciaDatasVigencia();
    }

    public void setDataFimVigencia(LocalDate dataFimVigencia) {
        this.dataFimVigencia = dataFimVigencia;
        validarConsistenciaDatasVigencia();
    }

    // Métodos de negócio
    public void ativar() {
        if (dataFimVigencia != null && LocalDate.now().isAfter(dataFimVigencia)) {
            throw new IllegalStateException("Não é possível ativar uma promoção cujo período de vigência (" + dataFimVigencia + ") já terminou.");
        }
        this.ativa = true;
    }

    public void desativar() {
        this.ativa = false;
    }

    private void validarConsistenciaDatasVigencia() {
        if (this.dataInicioVigencia != null && this.dataFimVigencia != null &&
                this.dataFimVigencia.isBefore(this.dataInicioVigencia)) {
            throw new IllegalStateException("Data de fim de vigência da promoção (" + this.dataFimVigencia +
                    ") não pode ser anterior à data de início (" + this.dataInicioVigencia + ").");
        }
    }

    /**
     * Verifica se a promoção está globalmente ativa E dentro do seu período de vigência.
     */
    public boolean estaVigente(LocalDate dataReferencia) {
        Objects.requireNonNull(dataReferencia, "Data de referência para vigência não pode ser nula.");
        if (!this.ativa) { // Se o flag 'ativa' está false, não está vigente.
            return false;
        }
        // Se não há datas de vigência, considera-se sempre vigente (se ativa)
        boolean inicioOk = (this.dataInicioVigencia == null) || !dataReferencia.isBefore(this.dataInicioVigencia);
        boolean fimOk = (this.dataFimVigencia == null) || !dataReferencia.isAfter(this.dataFimVigencia); // dataReferencia <= dataFim
        return inicioOk && fimOk;
    }

    public BigDecimal calcularDesconto(BigDecimal precoBase) {
        Objects.requireNonNull(precoBase, "Preço base não pode ser nulo para calcular desconto.");
        if (precoBase.compareTo(BigDecimal.ZERO) < 0) { // Permite preço base zero, mas não negativo
            throw new IllegalArgumentException("Preço base para cálculo de desconto não pode ser negativo. Recebido: " + precoBase);
        }

        // A verificação de estaVigente e aplicavel deve ser feita ANTES de chamar calcularDesconto.
        // No entanto, uma verificação de segurança aqui para o flag 'ativa' não faz mal.
        if (!this.ativa) return BigDecimal.ZERO;

        if (this.percentualDesconto != null) {
            return precoBase.multiply(this.percentualDesconto).setScale(2, RoundingMode.HALF_UP);
        } else if (this.valorDescontoFixo != null) {
            return this.valorDescontoFixo.min(precoBase); // Desconto não pode ser maior que o preço base
        }
        return BigDecimal.ZERO;
    }

    public boolean aplicavel(Cliente cliente, Sessao sessao, LocalDateTime dataHoraCompra) {
        Objects.requireNonNull(dataHoraCompra, "Data e hora da compra não podem ser nulas.");
        // Primeira verificação: a promoção em si está vigente na data da compra?
        if (!estaVigente(dataHoraCompra.toLocalDate())) {
            return false;
        }

        // Depois, verifica as condições específicas do tipo de promoção
        switch (this.tipoPromocao) {
            case MEIA_ENTRADA_PERFIL:
                Objects.requireNonNull(cliente, "Cliente é necessário para promoção de perfil.");
                // A promoção deve ter perfis aplicáveis definidos E o cliente deve ter um desses perfis.
                return this.perfisAplicaveis != null && !this.perfisAplicaveis.isEmpty() &&
                        this.perfisAplicaveis.contains(cliente.getPerfil()) &&
                        cliente.elegivelParaMeiaEntrada(); // Redundante se perfisAplicaveis já filtra, mas garante
            case HORARIO_BAIXA_DEMANDA:
                Objects.requireNonNull(sessao, "Sessão é necessária para promoção de horário.");
                LocalDateTime dataHoraSessao = sessao.getDataHoraInicio(); // Usar dataHoraInicio da sessão
                DayOfWeek diaSessao = dataHoraSessao.getDayOfWeek();
                LocalTime horaSessao = dataHoraSessao.toLocalTime();

                boolean diaAplicavel = this.diasDaSemanaAplicaveis == null || this.diasDaSemanaAplicaveis.isEmpty() ||
                        this.diasDaSemanaAplicaveis.contains(diaSessao);
                if (!diaAplicavel) return false;

                boolean horarioOk = true;
                if (this.horarioInicioAplicavel != null && horaSessao.isBefore(this.horarioInicioAplicavel)) {
                    horarioOk = false;
                }
                if (this.horarioFimAplicavel != null && horaSessao.isAfter(this.horarioFimAplicavel)) { // Ou isAfter/equals dependendo da regra
                    horarioOk = false;
                }
                return horarioOk;
            // Outros tipos de promoção (COMBO_ESPECIAL, NENHUMA) teriam suas lógicas aqui
            case NENHUMA:
            default:
                return false;
        }
    }

    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Promocao promocao = (Promocao) o; return id.equals(promocao.id); }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() { return "Promocao{id=" + id + ", nome='" + nomeDescritivo + "', tipo=" + tipoPromocao + ", ativa=" + ativa + "}"; }
}
