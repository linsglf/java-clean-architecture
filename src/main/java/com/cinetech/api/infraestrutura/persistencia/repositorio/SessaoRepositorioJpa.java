package com.cinetech.api.infraestrutura.persistencia.repositorio;

import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.dominio.modelos.sala.SalaId;
import com.cinetech.api.dominio.modelos.filme.FilmeId;
import com.cinetech.api.dominio.modelos.assento.Assento;
import com.cinetech.api.dominio.modelos.cliente.ClienteId; // Necessário para o construtor de Assento
import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.repositorios.SessaoRepositorio;
import com.cinetech.api.infraestrutura.persistencia.jpa.SessaoJpaRepository; // Interface Spring Data JPA
import com.cinetech.api.infraestrutura.persistencia.entidade.SessaoJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.AssentoJpa; // Para iterar
// Importe as CLASSES dos mappers para chamadas estáticas
import com.cinetech.api.infraestrutura.persistencia.mapper.SessaoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.AssentoMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.FilmeMapper;
import com.cinetech.api.infraestrutura.persistencia.mapper.SalaMapper;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SessaoRepositorioJpa implements SessaoRepositorio {

    private final SessaoJpaRepository jpaRepositoryInternal;
    // Mappers não são mais injetados

    public SessaoRepositorioJpa(SessaoJpaRepository jpaRepositoryInternal) {
        this.jpaRepositoryInternal = jpaRepositoryInternal;
    }

    // Método auxiliar para reconstruir o agregado Sessao com seus Assentos, usando chamadas estáticas aos mappers
    private static Sessao reconstruirAgregadoSessao(SessaoJpa sessaoJpa) {
        if (sessaoJpa == null) {
            return null;
        }

        // 1. Mapeia a Sessao (cabeçalho) usando o método estático do SessaoMapper.
        // Este método já deve chamar FilmeMapper e SalaMapper estaticamente para os campos Filme e Sala.
        Sessao sessaoDominio = SessaoMapper.toDomainEntity(sessaoJpa); // Presume que este retorna Sessao sem a lista de Assentos populada

        // 2. Constrói e adiciona os Assentos de domínio
        if (sessaoJpa.getAssentos() != null && !sessaoJpa.getAssentos().isEmpty() && sessaoDominio != null) {
            for (AssentoJpa assentoJpa : sessaoJpa.getAssentos()) {
                // Chama o método estático AssentoMapper.toDomainEntity,
                // passando a sessaoDominio pai como contexto.
                Assento assentoDominio = AssentoMapper.toDomainEntity(assentoJpa, sessaoDominio);
                sessaoDominio.adicionarAssento(assentoDominio); // Método da entidade Sessao
            }
        }
        return sessaoDominio;
    }

    private static List<Sessao> reconstruirListaAgregadosSessao(List<SessaoJpa> sessoesJpa) {
        if (sessoesJpa == null) {
            return Collections.emptyList();
        }
        return sessoesJpa.stream()
                .map(SessaoRepositorioJpa::reconstruirAgregadoSessao) // Chama o método estático local
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Sessao salvar(Sessao sessaoDominio) {
        SessaoJpa sessaoJpa = SessaoMapper.toJpaEntity(sessaoDominio); // Chamada estática
        // Garante o relacionamento bidirecional para JPA antes de salvar
        if (sessaoJpa.getAssentos() != null) {
            sessaoJpa.getAssentos().forEach(assentoJpa -> {
                if (assentoJpa.getSessao() == null) {
                    assentoJpa.setSessao(sessaoJpa);
                }
            });
        }
        SessaoJpa sessaoSalvaJpa = jpaRepositoryInternal.save(sessaoJpa);
        return reconstruirAgregadoSessao(sessaoSalvaJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sessao> buscarPorId(SessaoId sessaoIdDominio) {
        UUID idPrimitivo = SessaoMapper.toPrimitiveId(sessaoIdDominio); // Chamada estática
        return jpaRepositoryInternal.findById(idPrimitivo).map(SessaoRepositorioJpa::reconstruirAgregadoSessao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarPorSalaId(SalaId salaIdDominio) {
        UUID salaUUID = SalaMapper.toPrimitiveId(salaIdDominio); // Chamada estática
        return reconstruirListaAgregadosSessao(jpaRepositoryInternal.findBySala_Id(salaUUID));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarSessoesConflitantesPorSalaEPeriodo(
            SalaId salaId, LocalDateTime inicioPeriodoProposto, LocalDateTime fimPeriodoProposto,
            Optional<SessaoId> sessaoIdParaExcluirOptional) {

        UUID salaUUID = SalaMapper.toPrimitiveId(salaId); // Chamada estática
        UUID sessaoExcluirUUID = sessaoIdParaExcluirOptional
                .map(SessaoMapper::toPrimitiveId).orElse(null); // Chamada estática

        // Busca apenas os JpaEntities. A lógica de cálculo de fim de sessão e conflito fica aqui.
        List<SessaoJpa> sessoesNaSalaJpa = jpaRepositoryInternal.findBySala_Id(salaUUID);

        return sessoesNaSalaJpa.stream()
                .filter(sessaoExistenteJpa -> {
                    // Exclui a própria sessão da verificação, se um ID foi fornecido
                    if (sessaoExcluirUUID != null && sessaoExistenteJpa.getId().equals(sessaoExcluirUUID)) {
                        return false;
                    }
                    // Para calcular o fim da sessão, precisamos da duração do filme.
                    // Isso requer o FilmeJpa associado. Se for LAZY, pode causar N+1.
                    // Idealmente, FilmeJpa é carregado EAGER ou com JOIN FETCH na query findBySala_Id,
                    // ou o cálculo do conflito é feito de forma mais inteligente no banco.
                    // Assumindo que FilmeJpa está acessível:
                    if (sessaoExistenteJpa.getFilme() == null) return false; // Sessão inválida sem filme

                    LocalDateTime inicioSessaoExistente = sessaoExistenteJpa.getDataHoraInicio();
                    LocalDateTime fimSessaoExistente = inicioSessaoExistente
                            .plusMinutes(sessaoExistenteJpa.getFilme().getDuracaoMinutos());

                    return inicioPeriodoProposto.isBefore(fimSessaoExistente) &&
                            fimPeriodoProposto.isAfter(inicioSessaoExistente);
                })
                .map(SessaoRepositorioJpa::reconstruirAgregadoSessao) // Converte para domínio APÓS filtrar
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarTodas() {
        return reconstruirListaAgregadosSessao(jpaRepositoryInternal.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarSessoesAtivasPorFilmeId(FilmeId filmeIdDominio) {
        UUID filmeUUID = FilmeMapper.toPrimitiveId(filmeIdDominio); // Chamada estática
        List<StatusSessao> statusAtivos = List.of(StatusSessao.PROGRAMADA, StatusSessao.ABERTA);

        // Assume que este método existe em SessaoJpaRepository
        List<SessaoJpa> sessoesAtivasJpa = jpaRepositoryInternal.findByFilme_IdAndStatusInAndDataHoraInicioAfter(
                filmeUUID, statusAtivos, LocalDateTime.now()
        );
        return reconstruirListaAgregadosSessao(sessoesAtivasJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessao> buscarPorStatus(StatusSessao status) {
        // Assume que este método existe em SessaoJpaRepository
        return reconstruirListaAgregadosSessao(jpaRepositoryInternal.findByStatus(status));
    }
}