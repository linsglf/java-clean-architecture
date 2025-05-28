package com.cinetech.api.dominio.servicos.PrecificacaoServico;


import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.promocao.Promocao;
import com.cinetech.api.dominio.modelos.sessao.Sessao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects; // Importado para uso no Record
import java.util.Optional;

public interface PrecificacaoServico {

    /**
     * Representa o resultado do cálculo de precificação.
     * É um record para simplicidade e imutabilidade.
     */
    record ResultadoPrecificacao(
            BigDecimal precoOriginal,
            BigDecimal descontoAplicado,
            BigDecimal precoFinal,
            Promocao promocaoAplicada // Pode ser null se o desconto principal não veio de uma entidade Promocao (ex: meia-entrada legal direta)
    ) {
        public ResultadoPrecificacao { // Construtor compacto para validação
            Objects.requireNonNull(precoOriginal, "Preço original não pode ser nulo.");
            Objects.requireNonNull(descontoAplicado, "Desconto aplicado não pode ser nulo.");
            Objects.requireNonNull(precoFinal, "Preço final não pode ser nulo.");
            // promocaoAplicada pode ser nulo
        }

        public Optional<Promocao> getPromocaoAplicada() { // Método utilitário
            return Optional.ofNullable(promocaoAplicada);
        }
    }

    ResultadoPrecificacao calcularPrecoFinalIngresso(
            Cliente cliente,
            Sessao sessao,
            List<Promocao> promocoesVigentes,
            LocalDateTime dataHoraCompra
    );
}
