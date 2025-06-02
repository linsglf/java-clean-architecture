package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.dominio.enums.StatusAvaliacao;
import com.cinetech.api.infraestrutura.persistencia.entidade.AvaliacaoJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoJpaRepository extends JpaRepository<AvaliacaoJpa, UUID> {

    // Busca todas as avaliações para um determinado filme, usando o campo filmeId (UUID) da AvaliacaoJpa
    List<AvaliacaoJpa> findByFilmeId(UUID filmeId); // CORREÇÃO: de findByFilme_Id para findByFilmeId

    // Busca a avaliação de um cliente específico para um filme específico
    // Se AvaliacaoJpa tem clienteId (UUID) e filmeId (UUID)
    Optional<AvaliacaoJpa> findByClienteIdAndFilmeId(UUID clienteId, UUID filmeId); // CORREÇÃO: de findByCliente_IdAndFilme_Id

    // Busca todas as avaliações para um FilmeJpa com um StatusAvaliacao específico
    List<AvaliacaoJpa> findByFilmeIdAndStatusVisibilidade(UUID filmeId, StatusAvaliacao statusVisibilidade); // CORREÇÃO

    List<AvaliacaoJpa> findByStatusVisibilidade(StatusAvaliacao statusVisibilidade);
}