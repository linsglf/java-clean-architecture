package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.modelos.assento.Assento;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.dominio.repositorios.SessaoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.entidade.AssentoJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.SessaoJpa;
import com.cinetech.api.infraestrutura.persistencia.jpa.SessaoJpaRepository;

import com.cinetech.api.infraestrutura.persistencia.mapper.AssentoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.FilmeMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.SalaMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.SessaoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SessaoRepositorioJpa implements SessaoRepositorio {

    private final SessaoJpaRepository jpaRepository;
    private final SessaoMapper sessaoMapper;
    private final AssentoMapper assentoMapper;
    private final FilmeMapper filmeMapper;
    private final SalaMapper salaMapper;

    public SessaoRepositorioJpa(SessaoJpaRepository jpaRepository, SessaoMapper sessaoMapper,
                                AssentoMapper assentoMapper, FilmeMapper filmeMapper, SalaMapper salaMapper)
    {
        this.jpaRepository = jpaRepository;
        this.sessaoMapper = sessaoMapper;
        this.assentoMapper = assentoMapper;
        this.filmeMapper = filmeMapper;
        this.salaMapper = salaMapper;
    }

    private Sessao reconstruirAgregadoSessao(SessaoJpa sessaoJpa) {
        if (sessaoJpa == null) return null;

        // 1. Mapeia a Sessao (cabeçalho, sem os assentos de domínio ainda)
        // O SessaoMapper.toDomainEntity agora usa FilmeMapper e SalaMapper (via 'uses') para os campos Filme e Sala.
        Sessao sessaoDominio = sessaoMapper.toDomainEntity(sessaoJpa);

        // 2. Constrói a lista de Assentos de domínio e os adiciona à Sessao de domínio
        if (sessaoJpa.getAssentos() != null && !sessaoJpa.getAssentos().isEmpty()) {
            List<Assento> assentosDeDominio = new ArrayList<>();
            for (AssentoJpa assentoJpa : sessaoJpa.getAssentos()) {
                // Constrói a entidade Assento de domínio, passando a referência da Sessao de domínio pai
                Assento assentoDominio = new Assento(
                        assentoMapper.uuidToAssentoId(assentoJpa.getId()), // Usa o mapper para converter o ID
                        sessaoDominio, // <<< PASSA A REFERÊNCIA DA SESSÃO DE DOMÍNIO PAI
                        assentoJpa.getIdentificadorPosicao(),
                        assentoJpa.getTipo(),
                        assentoJpa.getStatus(),
                        assentoJpa.getClienteIdReservaTemporaria() != null ? ClienteId.de(assentoJpa.getClienteIdReservaTemporaria().toString()) : null,
                        assentoJpa.getTimestampExpiracaoReserva()
                );
                // Adiciona o Assento de domínio à lista
                sessaoDominio.adicionarAssento(assentoDominio);
            }
        }
        return sessaoDominio;
    }

    private List<Sessao> reconstruirListaAgregadosSessao(List<SessaoJpa> sessoesJpa) {
        if (sessoesJpa == null) return Collections.emptyList();
        return sessoesJpa.stream()
                .map(this::reconstruirAgregadoSessao)
                .filter(Objects::nonNull) // Para o caso de algum mapeamento falhar
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Sessao salvar(Sessao sessaoDominio) {
        SessaoJpa sessaoJpa = sessaoMapper.toJpaEntity(sessaoDominio);
        // Garante o relacionamento bidirecional para JPA antes de salvar
        if (sessaoJpa.getAssentos() != null) {
            sessaoJpa.getAssentos().forEach(assentoJpa -> {
                if (assentoJpa.getSessao() == null) { // Define a referência se o AssentoMapper não o fez
                    assentoJpa.setSessao(sessaoJpa);
                }
            });
        }
        SessaoJpa sessaoSalvaJpa = jpaRepository.save(sessaoJpa);
        return reconstruirAgregadoSessao(sessaoSalvaJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sessao> buscarPorId(SessaoId sessaoId) {
        UUID idPrimitivo = sessaoMapper.toPrimitiveId(sessaoId);
        return jpaRepository.findById(idPrimitivo).map(this::reconstruirAgregadoSessao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarPorSalaId(SalaId salaId) {
        // Assumindo que SalaMapper tem toPrimitiveId(SalaId salaIdVo)
        return reconstruirListaAgregadosSessao(jpaRepository.findBySala_Id(salaMapper.toPrimitiveId(salaId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarSessoesConflitantesPorSalaEPeriodo(
            SalaId salaId, LocalDateTime inicioPeriodoProposto, LocalDateTime fimPeriodoProposto,
            Optional<SessaoId> sessaoIdParaExcluirOptional) {

        List<SessaoJpa> sessoesNaSalaJpa = jpaRepository.findBySala_Id(salaMapper.toPrimitiveId(salaId));
        List<Sessao> sessoesConvertidas = reconstruirListaAgregadosSessao(sessoesNaSalaJpa);

        return sessoesConvertidas.stream()
                .filter(sessaoExistente -> {
                    if (sessaoIdParaExcluirOptional.isPresent() &&
                            sessaoExistente.getId().equals(sessaoIdParaExcluirOptional.get())) {
                        return false;
                    }
                    if (sessaoExistente.getFilme() == null) return false;
                    LocalDateTime inicioSessaoExistente = sessaoExistente.getDataHoraInicio();
                    LocalDateTime fimSessaoExistente = inicioSessaoExistente
                            .plusMinutes(sessaoExistente.getFilme().getDuracaoMinutos());
                    return inicioPeriodoProposto.isBefore(fimSessaoExistente) &&
                            fimPeriodoProposto.isAfter(inicioSessaoExistente);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarTodas() {
        return reconstruirListaAgregadosSessao(jpaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarSessoesAtivasPorFilmeId(FilmeId filmeId) {
        // Assumindo FilmeMapper.toPrimitiveId
        UUID filmeUUID = filmeMapper.toPrimitiveId(filmeId);
        List<StatusSessao> statusAtivos = List.of(StatusSessao.PROGRAMADA, StatusSessao.ABERTA);
        List<SessaoJpa> sessoesAtivasJpa = jpaRepository.findByFilme_IdAndStatusInAndDataHoraInicioAfter(
                filmeUUID, statusAtivos, LocalDateTime.now()
        );
        return reconstruirListaAgregadosSessao(sessoesAtivasJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarPorStatus(StatusSessao status) {
        return reconstruirListaAgregadosSessao(jpaRepository.findByStatus(status));
    }
}
