package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.infraestrutura.persistencia.entidade.IngressoJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IngressoJpaRepository extends JpaRepository<IngressoJpa, UUID> {
    Optional<IngressoJpa> findByCodigoValidacao(String codigoValidacao);
    List<IngressoJpa> findBySessao_Id(UUID sessaoId); // Busca por ID da SessaoJpa associada
    List<IngressoJpa> findByCliente_Id(UUID clienteId); // Busca por ID da ClienteJpa associada
}
