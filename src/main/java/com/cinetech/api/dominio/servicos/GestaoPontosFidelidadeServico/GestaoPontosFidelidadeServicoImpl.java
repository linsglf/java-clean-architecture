package com.cinetech.api.dominio.servicos.GestaoPontosFidelidadeServico;

import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public class GestaoPontosFidelidadeServicoImpl implements GestaoPontosFidelidadeServico {

    // Regra de negócio: 1 ponto a cada R$10 gastos.
    // Poderia ser configurável ou mais complexa.
    private static final BigDecimal FATOR_CONVERSAO_VALOR_PARA_PONTO = new BigDecimal("10.00");

    public GestaoPontosFidelidadeServicoImpl() {
        // Este serviço é stateless e opera sobre a entidade Cliente.
        // Não necessita de repositórios próprios se o Application Service
        // que o utiliza já carregou o Cliente e será responsável por salvá-lo.
    }

    @Override
    public void concederPontosPorCompra(Cliente cliente, BigDecimal valorPagoNoIngresso, IngressoId ingressoOrigemId) {
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo para concessão de pontos.");
        Objects.requireNonNull(valorPagoNoIngresso, "Valor pago no ingresso não pode ser nulo.");
        // ingressoOrigemId pode ser nulo

        if (valorPagoNoIngresso.compareTo(BigDecimal.ZERO) > 0) {
            int pontosGanhos = valorPagoNoIngresso
                    .divide(FATOR_CONVERSAO_VALOR_PARA_PONTO, 0, RoundingMode.FLOOR)
                    .intValue();

            if (pontosGanhos > 0) {
                // CORREÇÃO AQUI: Chamando o novo método em Cliente
                cliente.adicionarNovosPontos(pontosGanhos, ingressoOrigemId);
                System.out.println("INFO DOMINIO: " + pontosGanhos + " pontos concedidos ao cliente " + cliente.getId() + " pela compra do ingresso " + (ingressoOrigemId != null ? ingressoOrigemId : "N/A"));
            }
        }
        // O Application Service que chamou este método será responsável por persistir o Cliente atualizado.
    }

    @Override
    public void expirarPontosObsoletosParaCliente(Cliente cliente, LocalDate dataReferencia) {
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo para expiração de pontos.");
        Objects.requireNonNull(dataReferencia, "Data de referência para expiração não pode ser nula.");

        // Delega para o método da entidade Cliente, que manipula sua lista interna de pontos.
        // Esse método em Cliente iteraria sobre seus PontoFidelidade e os removeria/marcaria como expirados.
        cliente.expirarPontosFidelidadeObsoletos(dataReferencia);
        // O Application Service que chamou este método será responsável por persistir o Cliente atualizado.
    }

    @Override
    public void utilizarPontos(Cliente cliente, int pontosNecessarios, LocalDate dataReferencia) {
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo para utilização de pontos.");
        if (pontosNecessarios <= 0) {
            throw new IllegalArgumentException("Pontos necessários para utilização devem ser positivos.");
        }
        Objects.requireNonNull(dataReferencia, "Data de referência para utilização de pontos não pode ser nula.");

        // Delega para o método da entidade Cliente.
        // O método cliente.utilizarPontosFidelidade já lança IllegalStateException se não houver saldo.
        cliente.utilizarPontosFidelidade(pontosNecessarios, dataReferencia);
        // O Application Service que chamou este método será responsável por persistir o Cliente atualizado
        // e registrar a consequência do uso dos pontos (ex: gerar um ingresso com desconto, etc.).
    }
}
