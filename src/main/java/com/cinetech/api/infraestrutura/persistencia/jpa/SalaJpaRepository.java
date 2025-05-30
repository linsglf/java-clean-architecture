package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.infraestrutura.persistencia.entidade.SalaJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaJpaRepository extends JpaRepository<SalaJpa, UUID> {
    Optional<SalaJpa> findByNome(String nome);
    List<SalaJpa> findByDisponivelParaEventosTrue();
}
