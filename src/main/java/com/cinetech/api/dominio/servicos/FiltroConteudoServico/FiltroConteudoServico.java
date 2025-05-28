package com.cinetech.api.dominio.servicos.FiltroConteudoServico;

import com.cinetech.api.dominio.enums.StatusAvaliacao;

public interface FiltroConteudoServico {

    /**
     * Analisa o texto de um comentário e sugere um status de visibilidade para a avaliação.
     *
     * @param comentario O texto do comentário a ser analisado.
     * @return Um StatusAvaliacao sugerido (ex: PENDENTE_MODERACAO, APROVADA, REPROVADA_OFENSIVA).
     */
    StatusAvaliacao analisarComentarioESugerirStatus(String comentario);
}
