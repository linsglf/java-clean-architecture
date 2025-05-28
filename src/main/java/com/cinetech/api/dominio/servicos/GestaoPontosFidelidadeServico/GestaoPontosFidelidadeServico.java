package com.cinetech.api.dominio.servicos.GestaoPontosFidelidadeServico;

import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;

import java.math.BigDecimal; // Para o valor do ingresso
import java.time.LocalDate;

public interface GestaoPontosFidelidadeServico {

    /**
     * Calcula e orienta a adição de pontos de fidelidade a um cliente com base em uma compra de ingresso.
     * Este método determina quantos pontos devem ser concedidos e chama o método apropriado
     * na entidade Cliente para adicionar esses pontos.
     *
     * @param cliente O cliente que realizou a compra (será modificado).
     * @param valorPagoNoIngresso O valor efetivamente pago pelo ingresso que originou os pontos.
     * @param ingressoOrigemId O ID do ingresso que originou os pontos (para rastreabilidade).
     */
    void concederPontosPorCompra(Cliente cliente, BigDecimal valorPagoNoIngresso, IngressoId ingressoOrigemId);

    /**
     * Processa a expiração de pontos para um cliente específico, baseado na data de referência.
     * Modifica a lista de pontos na entidade Cliente.
     *
     * @param cliente O cliente para o qual os pontos serão verificados/expirados (será modificado).
     * @param dataReferencia A data base para verificar a expiração (pontos com dataExpiracao < dataReferencia).
     */
    void expirarPontosObsoletosParaCliente(Cliente cliente, LocalDate dataReferencia);

    /**
     * Tenta utilizar pontos de fidelidade de um cliente para um determinado benefício/resgate.
     * Modifica a lista de pontos na entidade Cliente se a utilização for bem-sucedida.
     *
     * @param cliente O cliente utilizando os pontos (será modificado).
     * @param pontosNecessarios A quantidade de pontos a ser utilizada.
     * @param dataReferencia A data atual para verificar a validade dos pontos.
     * @throws IllegalStateException se o saldo de pontos válidos for insuficiente.
     */
    void utilizarPontos(Cliente cliente, int pontosNecessarios, LocalDate dataReferencia);
}
