package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.dominio.enums.StatusReservaEvento;
import com.cinetech.api.infraestrutura.persistencia.entidade.ReservaEventoJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository // Boa prática anotar, embora o Spring Data JPA as detecte
public interface ReservaEventoJpaRepository extends JpaRepository<ReservaEventoJpa, UUID> {

    /**
     * Busca todas as reservas de evento para uma determinada SalaJpa, usando o ID da sala.
     * Spring Data JPA infere a query pelo nome do método.
     * @param salaId O UUID da SalaJpa.
     * @return Uma lista de ReservaEventoJpa.
     */
    List<ReservaEventoJpa> findBySala_Id(UUID salaId);

    /**
     * Busca todas as reservas de evento para um determinado ClienteJpa, usando o ID do cliente.
     * @param clienteId O UUID do ClienteJpa.
     * @return Uma lista de ReservaEventoJpa.
     */
    List<ReservaEventoJpa> findByCliente_Id(UUID clienteId);

    /**
     * Busca reservas de evento por um status específico.
     * @param status O StatusReservaEvento a ser buscado.
     * @return Uma lista de ReservaEventoJpa com o status especificado.
     */
    List<ReservaEventoJpa> findByStatus(StatusReservaEvento status);

    /**
     * Query para buscar reservas de evento em uma determinada sala que conflitam
     * com um período especificado, opcionalmente excluindo uma ReservaEventoId específica.
     * Este método é usado pelo AgendamentoService.
     * A lógica de conflito é (StartA < EndB) AND (EndA > StartB).
     *
     * @param salaId UUID da SalaJpa.
     * @param inicioPeriodo Início do período proposto.
     * @param fimPeriodo Fim do período proposto.
     * @param reservaIdParaExcluir UUID da ReservaEventoJpa a ser excluída da verificação (pode ser null).
     * @return Lista de ReservaEventoJpa conflitantes.
     */
    @Query("SELECT r FROM ReservaEventoJpa r WHERE r.sala.id = :salaId " +
            "AND (:reservaIdParaExcluir IS NULL OR r.id <> :reservaIdParaExcluir) " +
            "AND (r.dataHoraInicio < :fimPeriodo AND r.dataHoraFim > :inicioPeriodo)")
    List<ReservaEventoJpa> findReservasConflitantes(
            @Param("salaId") UUID salaId,
            @Param("inicioPeriodo") LocalDateTime inicioPeriodo,
            @Param("fimPeriodo") LocalDateTime fimPeriodo,
            @Param("reservaIdParaExcluir") UUID reservaIdParaExcluir
    );
}
