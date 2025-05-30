package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.infraestrutura.persistencia.entidade.SessaoJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // Necessário para o método que ficou
import java.util.List;
import java.util.UUID;

@Repository
public interface SessaoJpaRepository extends JpaRepository<SessaoJpa, UUID> {

    // Busca todas as sessões para uma determinada sala.
    // A filtragem de conflito e exclusão será feita em Java.
    List<SessaoJpa> findBySala_Id(UUID salaId);

    // Outros métodos que já tínhamos:
    List<SessaoJpa> findByFilme_IdAndStatusInAndDataHoraInicioAfter(
            UUID filmeId, List<StatusSessao> status, LocalDateTime dataReferencia
    );

    List<SessaoJpa> findByStatus(StatusSessao status);
}
