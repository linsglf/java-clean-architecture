package com.cinetech.api.dominio.servicos.PrecificacaoServico;

import com.cinetech.api.dominio.enums.TipoPromocao;
import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.promocao.Promocao;
import com.cinetech.api.dominio.modelos.sessao.Sessao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PrecificacaoServicoImpl implements PrecificacaoServico {
    private static final BigDecimal FATOR_MEIA_ENTRADA_LEGAL = new BigDecimal("0.50"); // 50%

    @Override
    public ResultadoPrecificacao calcularPrecoFinalIngresso(
            Cliente cliente, Sessao sessao, List<Promocao> promocoesVigentes, LocalDateTime dataHoraCompra) {

        Objects.requireNonNull(cliente, "Cliente não pode ser nulo.");
        Objects.requireNonNull(sessao, "Sessão não pode ser nula.");
        Objects.requireNonNull(promocoesVigentes, "Lista de promoções vigentes não pode ser nula.");
        Objects.requireNonNull(dataHoraCompra, "Data e hora da compra não podem ser nulas.");

        BigDecimal precoBase = sessao.getPrecoIngressoBase();
        BigDecimal melhorDesconto = BigDecimal.ZERO;
        Promocao promocaoSelecionadaParaMelhorDesconto = null;

        // 1. Avaliar todas as promoções configuradas no sistema
        for (Promocao promocao : promocoesVigentes) {
            if (promocao.estaVigente(dataHoraCompra.toLocalDate()) && // Garante que a promoção em si está ativa
                    promocao.aplicavel(cliente, sessao, dataHoraCompra)) { // Verifica condições específicas

                BigDecimal descontoAtual = promocao.calcularDesconto(precoBase);
                if (descontoAtual.compareTo(melhorDesconto) > 0) {
                    melhorDesconto = descontoAtual;
                    promocaoSelecionadaParaMelhorDesconto = promocao;
                }
            }
        }

        // 2. Considerar a meia-entrada legal (F5)
        // A política aqui é que se o cliente é elegível para meia-entrada por lei,
        // e esse desconto for MAIOR que qualquer outra promoção encontrada, ele prevalece.
        if (cliente.elegivelParaMeiaEntrada()) {
            BigDecimal descontoMeiaLei = precoBase.multiply(FATOR_MEIA_ENTRADA_LEGAL).setScale(2, RoundingMode.HALF_UP);
            if (descontoMeiaLei.compareTo(melhorDesconto) > 0) {
                melhorDesconto = descontoMeiaLei;
                // Se a meia-entrada legal for aplicada como o melhor desconto,
                // e ela não for representada por uma das entidades `Promocao` na lista
                // (ex: se `promocaoSelecionadaParaMelhorDesconto` era de outro tipo ou menor),
                // então `promocaoSelecionadaParaMelhorDesconto` pode ser setada como null ou
                // para uma Promocao específica que representa a "Meia-Entrada Legal" se tal entidade existir.
                // Por simplicidade, se a meia-entrada legal ganhar, ela "sobrescreve" a seleção anterior
                // se não houver uma entidade Promocao de MEIA_ENTRADA_PERFIL que já deu esse desconto.
                if (promocaoSelecionadaParaMelhorDesconto == null ||
                        promocaoSelecionadaParaMelhorDesconto.getTipoPromocao() != TipoPromocao.MEIA_ENTRADA_PERFIL ||
                        promocaoSelecionadaParaMelhorDesconto.calcularDesconto(precoBase).compareTo(descontoMeiaLei) < 0) {
                    // Se a melhor promoção encontrada não era a de perfil ou era pior que a meia legal
                    promocaoSelecionadaParaMelhorDesconto = null; // Indica que o desconto aplicado foi a "meia legal" genérica
                    // A entidade Ingresso tem `meiaEntradaAplicada` (boolean) para registrar isso.
                }
            }
        }

        BigDecimal precoFinal = precoBase.subtract(melhorDesconto);
        if (precoFinal.compareTo(BigDecimal.ZERO) < 0) {
            precoFinal = BigDecimal.ZERO;
        }

        return new ResultadoPrecificacao(precoBase, melhorDesconto, precoFinal, promocaoSelecionadaParaMelhorDesconto);
    }
}
