package com.cinetech.api.dominio.repositorios;

import com.cinetech.api.dominio.enums.TipoPromocao;
import com.cinetech.api.dominio.modelos.promocao.Promocao;
import com.cinetech.api.dominio.modelos.promocao.PromocaoId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromocaoRepositorio {

    /**
     * Salva ou atualiza uma promoção.
     * @param promocao A entidade Promocao a ser salva.
     * @return A entidade Promocao salva.
     */
    Promocao salvar(Promocao promocao);

    /**
     * Busca uma promoção pelo seu ID.
     * @param promocaoId O ID da promoção.
     * @return Um Optional contendo a Promocao se encontrada.
     */
    Optional<Promocao> buscarPorId(PromocaoId promocaoId);

    /**
     * Busca uma promoção pelo seu nome descritivo.
     * @param nomeDescritivo O nome da promoção.
     * @return Um Optional contendo a Promocao se encontrada.
     */
    Optional<Promocao> buscarPorNomeDescritivo(String nomeDescritivo);

    /**
     * Lista todas as promoções cadastradas.
     * @return Uma lista de todas as promoções.
     */
    List<Promocao> buscarTodas();

    /**
     * Lista todas as promoções que estão ativas e vigentes em uma determinada data.
     * Uma promoção é vigente se:
     * - Seu flag 'ativa' é true.
     * - A dataReferencia está dentro do período [dataInicioVigencia, dataFimVigencia] (se definidos).
     * @param dataReferencia A data para a qual verificar a vigência.
     * @return Uma lista de promoções vigentes.
     */
    List<Promocao> buscarPromocoesVigentes(LocalDate dataReferencia);

    /**
     * Lista todas as promoções de um tipo específico.
     * @param tipoPromocao O tipo da promoção.
     * @return Uma lista de promoções do tipo especificado.
     */
    List<Promocao> buscarPorTipo(TipoPromocao tipoPromocao);

    // void deletarPorId(PromocaoId promocaoId);
}
