package com.cinetech.api.dominio.servicos.AgendamentoServico;

import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AgendamentoServico {
    boolean verificarConflitoAgendamento(
            SalaId salaId,
            LocalDateTime inicioProposto,
            LocalDateTime fimProposto,
            Optional<SessaoId> sessaoIdExcluida, // Para ignorar a própria sessão ao editar
            Optional<ReservaEventoId> reservaEventoIdExcluida // Para ignorar a própria reserva ao editar
    );
}
