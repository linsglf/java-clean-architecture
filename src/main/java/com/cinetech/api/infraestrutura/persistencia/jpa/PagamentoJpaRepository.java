package com.cinetech.api.infraestrutura.persistencia.jpa;

import com.cinetech.api.dominio.enums.StatusPagamento;
import com.cinetech.api.infraestrutura.persistencia.entidade.PagamentoJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagamentoJpaRepository extends JpaRepository<PagamentoJpa, UUID> {

    /**
     * Busca um pagamento pelo UUID do IngressoId associado.
     * Note que PagamentoJpa armazena ingressoId como UUID.
     */
    Optional<PagamentoJpa> findByIngressoId(UUID ingressoId);

    /**
     * Busca um pagamento pelo UUID do ReservaEventoId associado.
     * Note que PagamentoJpa armazena reservaEventoId como UUID.
     */
    Optional<PagamentoJpa> findByReservaEventoId(UUID reservaEventoId);

    /**
     * Busca pagamentos por um ID de transação do gateway externo.
     */
    Optional<PagamentoJpa> findByIdTransacaoGateway(String idTransacaoGateway);

    /**
     * Lista todos os pagamentos com um determinado status.
     */
    List<PagamentoJpa> findByStatus(StatusPagamento status);
}