package com.cinetech.api.dominio.servicos.FiltroConteudoServico;

import com.cinetech.api.dominio.enums.StatusAvaliacao;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FiltroConteudoServicoImpl implements FiltroConteudoServico {
    // Lista de exemplo, MUITO simplista. Um sistema real usaria técnicas mais robustas.
    private static final List<String> PALAVRAS_CHAVE_OFENSIVAS = Arrays.asList(
            "palavrafeia", "ofensateste", "insultoexemplo", "chulo"
            // Em um sistema real, esta lista seria configurável, mais extensa,
            // ou usaria regex, ou um serviço de IA para análise de sentimento/toxicidade.
    );

    @Override
    public StatusAvaliacao analisarComentarioESugerirStatus(String comentario) {
        if (comentario == null || comentario.trim().isEmpty()) {
            // Comentário vazio geralmente é permitido e não ofensivo.
            // Se a regra for que comentário é obrigatório, essa validação ocorreria antes.
            return StatusAvaliacao.APROVADA;
        }

        String comentarioNormalizado = comentario.toLowerCase(Locale.ROOT).trim();

        for (String palavraOfensiva : PALAVRAS_CHAVE_OFENSIVAS) {
            if (comentarioNormalizado.contains(palavraOfensiva.toLowerCase(Locale.ROOT))) {
                // "ocultar automaticamente comentários inapropriados" (Source 21)
                return StatusAvaliacao.REPROVADA_OFENSIVA;
            }
        }

        // Se não encontrou palavras ofensivas explicitamente, pode ser aprovado
        // ou ir para uma moderação manual se a política for mais rigorosa.
        // Para "filtro automático de conteúdo ofensivo", se não é ofensivo, é aprovado.
        return StatusAvaliacao.APROVADA;
    }
}
