package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteJpa, UUID> {
    Optional<ClienteJpa> findByCpf(String cpf);
    Optional<ClienteJpa> findByEmail(String email);
    // O método existsById(ID id) já é fornecido pelo JpaRepository
}
