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
import java.util.Optional;

public class PrecificacaoServicoImpl implements PrecificacaoServico {

    private static final BigDecimal FATOR_MEIA_ENTRADA_LEGAL = new BigDecimal("0.50");

    @Override
    public ResultadoPrecificacao calcularPrecoFinalIngresso(
            Cliente cliente, Sessao sessao, List<Promocao> promocoesVigentes, LocalDateTime dataHoraCompra) {

        Objects.requireNonNull(cliente, "Cliente não pode ser nulo.");
        Objects.requireNonNull(sessao, "Sessão não pode ser nula.");
        Objects.requireNonNull(promocoesVigentes, "Lista de promoções vigentes não pode ser nula.");
        Objects.requireNonNull(dataHoraCompra, "Data e hora da compra não podem ser nulas.");

        BigDecimal precoBase = sessao.getPrecoIngressoBase();
        BigDecimal melhorDescontoCalculado = BigDecimal.ZERO;
        Promocao promocaoSelecionada = null;

        // 1. Avaliar todas as promoções configuradas no sistema
        for (Promocao promocao : promocoesVigentes) {
            if (promocao.estaVigente(dataHoraCompra.toLocalDate()) &&
                    promocao.aplicavel(cliente, sessao, dataHoraCompra)) {

                BigDecimal descontoDaPromocaoAtual = promocao.calcularDesconto(precoBase);
                if (descontoDaPromocaoAtual.compareTo(melhorDescontoCalculado) > 0) {
                    melhorDescontoCalculado = descontoDaPromocaoAtual;
                    promocaoSelecionada = promocao;
                }
            }
        }

        // 2. Considerar a meia-entrada legal (F5) se o cliente for elegível
        // e verificar se ela é melhor que a melhor promoção encontrada até agora.
        boolean aplicouMeiaEntradaLegalComoMelhor = false;
        if (cliente.elegivelParaMeiaEntrada()) {
            BigDecimal descontoMeiaLei = precoBase.multiply(FATOR_MEIA_ENTRADA_LEGAL).setScale(2, RoundingMode.HALF_UP);
            if (descontoMeiaLei.compareTo(melhorDescontoCalculado) > 0) {
                melhorDescontoCalculado = descontoMeiaLei;
                // Se a meia-entrada legal foi melhor, tentamos encontrar uma promoção de perfil
                // que corresponda a este desconto para popular 'promocaoSelecionada'.
                // Se não houver uma específica, 'promocaoSelecionada' pode ficar nula,
                // e a lógica do CompraAplicacao não marcaria 'meiaAplicada' como true.
                // Para a Opção 2 funcionar, precisamos que uma Promocao de MEIA_ENTRADA_PERFIL seja "selecionada".
                Optional<Promocao> promocaoDePerfilCorrespondente = promocoesVigentes.stream()
                        .filter(p -> p.getTipoPromocao() == TipoPromocao.MEIA_ENTRADA_PERFIL &&
                                p.estaVigente(dataHoraCompra.toLocalDate()) &&
                                p.aplicavel(cliente, sessao, dataHoraCompra) && // Redundante se já passou pelo loop, mas seguro
                                p.calcularDesconto(precoBase).compareTo(descontoMeiaLei) == 0)
                        .findFirst();

                promocaoSelecionada = promocaoDePerfilCorrespondente.orElse(null); // Se não achar uma específica, fica null
                // o que fará a lógica do usuário em CompraAplicacao dar false.
                // Isso implica que DEVE existir uma Promoção de Perfil ativa
                // para que a flag meiaAplicada seja true com a lógica do usuário.
                aplicouMeiaEntradaLegalComoMelhor = (promocaoSelecionada != null); // Ou se o desconto é 50%
            }
        }

        // Se, após tudo, a promocaoSelecionada é de fato MEIA_ENTRADA_PERFIL,
        // então 'aplicouMeiaEntradaLegalComoMelhor' deve ser true para o ResultadoPrecificacao
        // (se fôssemos usar o campo booleano que sugeri antes).
        // o que importa é o tipo da 'promocaoSelecionada'.
        // Se 'promocaoSelecionada' for nula, mas 'melhorDescontoCalculado' for 50% devido à elegibilidade,
        // a lógica do usuário em CompraAplicacao ainda falhará em marcar meiaAplicada=true.

        // Para que funcione de forma mais robusta para a F5,
        // se 'melhorDescontoCalculado' corresponder ao desconto de meia-entrada legal,
        // 'promocaoSelecionada' DEVE ser uma instância de Promocao com tipo MEIA_ENTRADA_PERFIL.
        // Isso pode significar que o sistema DEVE ter uma promoção genérica de "Meia-Entrada Legal"
        // cadastrada com tipo MEIA_ENTRADA_PERFIL.

        // Lógica final de ajuste para `promocaoSelecionada`:
        if (cliente.elegivelParaMeiaEntrada()) {
            BigDecimal descontoMeiaLei = precoBase.multiply(FATOR_MEIA_ENTRADA_LEGAL).setScale(2, RoundingMode.HALF_UP);
            if (melhorDescontoCalculado.compareTo(descontoMeiaLei) == 0) { // Se o melhor desconto É a meia entrada
                // Tenta encontrar uma promocao de perfil que corresponda, se não, a promocaoSelecionada atual (se houver) é mantida
                // ou se a promocaoSelecionada atual não for de perfil, tentamos achar uma de perfil.
                Optional<Promocao> promocaoDePerfilExistente = promocoesVigentes.stream()
                        .filter(p -> p.getTipoPromocao() == TipoPromocao.MEIA_ENTRADA_PERFIL &&
                                p.estaVigente(dataHoraCompra.toLocalDate()) &&
                                p.aplicavel(cliente, sessao, dataHoraCompra))
                        .findFirst();
                if (promocaoDePerfilExistente.isPresent()) {
                    promocaoSelecionada = promocaoDePerfilExistente.get();
                } else if (promocaoSelecionada != null && promocaoSelecionada.getTipoPromocao() != TipoPromocao.MEIA_ENTRADA_PERFIL) {
                    // Se o melhor desconto foi de 50% (meia lei), mas a promocaoSelecionada é de outro tipo,
                    // para funcionar, teríamos que anular promocaoSelecionada ou
                    // ter uma "Promoção Meia-Entrada Legal" padrão.
                    // Se anulamos, falha.
                    // Conclusão: Para funcionar, DEVE haver uma Promoção MEIA_ENTRADA_PERFIL elegível.
                }
            }
        }


        BigDecimal precoFinal = precoBase.subtract(melhorDescontoCalculado);
        if (precoFinal.compareTo(BigDecimal.ZERO) < 0) {
            precoFinal = BigDecimal.ZERO;
        }

        return new PrecificacaoServico.ResultadoPrecificacao(precoBase, melhorDescontoCalculado, precoFinal, promocaoSelecionada);
    }
}
