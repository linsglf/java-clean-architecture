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

    /**
     * Busca todas as avaliações para um determinado FilmeJpa, usando o ID do filme.
     */
    List<AvaliacaoJpa> findByFilme_Id(UUID filmeId);

    /**
     * Busca a avaliação de um ClienteJpa específico para um FilmeJpa específico.
     */
    Optional<AvaliacaoJpa> findByCliente_IdAndFilme_Id(UUID clienteId, UUID filmeId);

    /**
     * Busca todas as avaliações para um FilmeJpa com um StatusAvaliacao específico.
     * Útil para buscar apenas as APROVADAS para exibição.
     */
    List<AvaliacaoJpa> findByFilme_IdAndStatusVisibilidade(UUID filmeId, StatusAvaliacao statusVisibilidade);

    /**
     * Busca todas as avaliações com um StatusAvaliacao específico.
     * Útil para painéis de moderação.
     */
    List<AvaliacaoJpa> findByStatusVisibilidade(StatusAvaliacao statusVisibilidade);
}
