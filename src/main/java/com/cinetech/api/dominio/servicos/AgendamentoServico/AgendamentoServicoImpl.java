package com.cinetech.api.dominio.servicos.AgendamentoServico;

import com.cinetech.api.dominio.modelos.reservaevento.ReservaEvento;
import com.cinetech.api.dominio.modelos.reservaevento.ReservaEventoId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.dominio.repositorios.ReservaEventoRepositorio;
import com.cinetech.api.dominio.repositorios.SessaoRepositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AgendamentoServicoImpl implements AgendamentoServico {

    private final SessaoRepositorio sessaoRepository;
    private final ReservaEventoRepositorio reservaEventoRepository;

    // Construtor para injetar dependências dos repositórios do domínio
    public AgendamentoServicoImpl(SessaoRepositorio sessaoRepository, ReservaEventoRepositorio reservaEventoRepository) {
        this.sessaoRepository = Objects.requireNonNull(sessaoRepository);
        this.reservaEventoRepository = Objects.requireNonNull(reservaEventoRepository);
    }

    @Override
    public boolean verificarConflitoAgendamento(
            SalaId salaId, LocalDateTime inicioProposto, LocalDateTime fimProposto,
            Optional<SessaoId> sessaoIdExcluida, Optional<ReservaEventoId> reservaEventoIdExcluida) {

        Objects.requireNonNull(salaId, "ID da Sala não pode ser nulo.");
        Objects.requireNonNull(inicioProposto, "Início proposto não pode ser nulo.");
        Objects.requireNonNull(fimProposto, "Fim proposto não pode ser nulo.");
        if (fimProposto.isBefore(inicioProposto) || fimProposto.equals(inicioProposto)) {
            throw new IllegalArgumentException("Fim proposto deve ser após o início proposto.");
        }

        // 1. Verificar conflitos com outras Sessões
        List<Sessao> sessoesConflitantes = sessaoRepository.buscarSessoesConflitantesPorSalaEPeriodo(
                salaId, inicioProposto, fimProposto, sessaoIdExcluida
        );
        if (!sessoesConflitantes.isEmpty()) {
            return true; // Há conflito com sessões
        }

        // 2. Verificar conflitos com outras Reservas de Evento
        List<ReservaEvento> reservasConflitantes = reservaEventoRepository.buscarReservasEventoConflitantesPorSalaEPeriodo(
                salaId, inicioProposto, fimProposto, reservaEventoIdExcluida
        );
        if (!reservasConflitantes.isEmpty()) {
            return true; // Há conflito com reservas de evento
        }

        return false; // Sem conflitos encontrados
    }
}
