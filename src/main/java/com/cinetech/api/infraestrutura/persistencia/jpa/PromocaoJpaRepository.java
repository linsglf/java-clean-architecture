package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.dominio.enums.TipoPromocao;
import com.cinetech.api.infraestrutura.persistencia.entidade.PromocaoJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromocaoJpaRepository extends JpaRepository<PromocaoJpa, UUID> {

    Optional<PromocaoJpa> findByNomeDescritivo(String nomeDescritivo);

    List<PromocaoJpa> findByTipoPromocao(TipoPromocao tipoPromocao);

    // Query para buscar promoções ativas e dentro do período de vigência
    @Query("SELECT p FROM PromocaoJpa p WHERE p.ativa = true " +
            "AND (p.dataInicioVigencia IS NULL OR p.dataInicioVigencia <= :dataReferencia) " +
            "AND (p.dataFimVigencia IS NULL OR p.dataFimVigencia >= :dataReferencia)")
    List<PromocaoJpa> findPromocoesVigentes(@Param("dataReferencia") LocalDate dataReferencia);
}
