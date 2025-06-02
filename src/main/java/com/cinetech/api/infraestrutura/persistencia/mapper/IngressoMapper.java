package com.cinetech.api.infraestrutura.persistencia.mapper;

import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.ingresso.Ingresso;
import com.cinetech.api.dominio.modelos.ingresso.IngressoId;
import com.cinetech.api.dominio.modelos.cliente.Cliente; // Objeto de domínio
import com.cinetech.api.dominio.modelos.sessao.Sessao;   // Objeto de domínio
import com.cinetech.api.dominio.modelos.assento.Assento; // Objeto de domínio
import com.cinetech.api.dominio.modelos.promocao.PromocaoId; // VO
import com.cinetech.api.infraestrutura.persistencia.entidade.IngressoJpa;
import com.cinetech.api.infraestrutura.persistencia.entidade.ClienteJpa;   // Para referência
import com.cinetech.api.infraestrutura.persistencia.entidade.SessaoJpa;    // Para referência
import com.cinetech.api.infraestrutura.persistencia.entidade.AssentoJpa;   // Para referência

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IngressoMapper {

    private IngressoMapper() {}

    // Método auxiliar para converter IngressoId (VO) para UUID
    public static UUID toPrimitiveId(IngressoId ingressoIdVo) {
        return ingressoIdVo == null ? null : ingressoIdVo.getValor();
    }

    public static IngressoJpa toJpaEntity(Ingresso domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        IngressoJpa jpaEntity = new IngressoJpa();
        if (domainEntity.getId() != null) {
            jpaEntity.setId(domainEntity.getId().getValor());
        }

        if (domainEntity.getCliente() != null && domainEntity.getCliente().getId() != null) {
            ClienteJpa clienteRef = new ClienteJpa();
            clienteRef.setId(domainEntity.getCliente().getId().getValor());
            jpaEntity.setCliente(clienteRef);
        }

        if (domainEntity.getSessao() != null && domainEntity.getSessao().getId() != null) {
            SessaoJpa sessaoRef = new SessaoJpa();
            sessaoRef.setId(domainEntity.getSessao().getId().getValor());
            jpaEntity.setSessao(sessaoRef);
        }

        if (domainEntity.getAssento() != null && domainEntity.getAssento().getId() != null) {
            AssentoJpa assentoRef = new AssentoJpa();
            assentoRef.setId(domainEntity.getAssento().getId().getValor());
            // O AssentoJpa também precisa da referência da SessaoJpa.
            // Isso pode ser setado aqui se o AssentoJpa tiver um setSessao,
            // ou o JPA pode lidar com isso se a relação for bem definida (ex: @JoinColumn em AssentoJpa.sessao)
            // e a SessaoJpa já estiver associada ao contexto de persistência.
            // Por segurança, se AssentoJpa tiver setSessao(SessaoJpa), poderíamos fazer:
            if (jpaEntity.getSessao() != null) { // Usa a SessaoJpa já criada para o IngressoJpa
                assentoRef.setSessao(jpaEntity.getSessao());
            }
            jpaEntity.setAssento(assentoRef);
        }

        jpaEntity.setValorPago(domainEntity.getValorPago());
        jpaEntity.setDataCompra(domainEntity.getDataCompra());
        jpaEntity.setMeiaEntradaAplicada(domainEntity.isMeiaEntradaAplicada());
        if (domainEntity.getPromocaoAplicadaId() != null) {
            jpaEntity.setPromocaoAplicadaId(domainEntity.getPromocaoAplicadaId().getValor());
        }
        jpaEntity.setCodigoValidacao(domainEntity.getCodigoValidacao());
        jpaEntity.setValidadoNaEntrada(domainEntity.isValidadoNaEntrada());

        return jpaEntity;
    }

    // O método toDomainEntity para Ingresso é complexo porque Ingresso (domínio)
    // espera instâncias completas de Cliente, Sessao, e Assento (domínio) no seu construtor.
    // Isso significa que este mapper precisaria chamar os mappers correspondentes.
    public static Ingresso toDomainEntity(IngressoJpa jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Cliente clienteDominio = null;
        if (jpaEntity.getCliente() != null) {
            clienteDominio = ClienteMapper.toDomainEntity(jpaEntity.getCliente());
        }

        Sessao sessaoDominio = null;
        if (jpaEntity.getSessao() != null) {
            // Importante: SessaoMapper.toDomainEntity aqui NÃO populava os assentos da sessão.
            // Se o Ingresso precisa de uma Sessao com assentos, a lógica fica mais complexa
            // ou o Application Service que carrega o Ingresso garante que a Sessao associada
            // seja carregada completamente.
            // Para o construtor de Ingresso, ele espera Sessao e Assento.
            // O SessaoRepositorioJpa é quem monta a Sessao com seus Assentos.
            // Aqui, o SessaoMapper só pode fornecer o "header" da Sessao.
            sessaoDominio = SessaoMapper.toDomainEntity(jpaEntity.getSessao());
        }

        Assento assentoDominio = null;
        if (jpaEntity.getAssento() != null && sessaoDominio != null) {
            // Precisamos construir o Assento de domínio com a referência à Sessao de domínio.
            // Isso idealmente seria feito por AssentoMapper.toDomainEntity(AssentoJpa, Sessao).
            // Como estamos fazendo manual aqui:
            assentoDominio = new Assento(
                    AssentoMapper.uuidToAssentoId(jpaEntity.getAssento().getId()), // Usa helper do AssentoMapper
                    sessaoDominio,
                    jpaEntity.getAssento().getIdentificadorPosicao(),
                    jpaEntity.getAssento().getTipo(),
                    jpaEntity.getAssento().getStatus(),
                    jpaEntity.getAssento().getClienteIdReservaTemporaria() != null ?
                            ClienteId.de(jpaEntity.getAssento().getClienteIdReservaTemporaria()) : null,
                    jpaEntity.getAssento().getTimestampExpiracaoReserva()
            );
            // E adicionar este assento à lista da sessaoDominio se ela não o tiver,
            // mas isso é mais responsabilidade do carregamento do agregado Sessao.
            // Por ora, o Ingresso apenas mantém a referência.
        }


        PromocaoId promocaoIdDominio = null;
        if (jpaEntity.getPromocaoAplicadaId() != null) {
            promocaoIdDominio = PromocaoId.de(jpaEntity.getPromocaoAplicadaId());
        }

        return new Ingresso(
                IngressoId.de(jpaEntity.getId()),
                clienteDominio,
                sessaoDominio,
                assentoDominio,
                jpaEntity.getValorPago(),
                jpaEntity.getDataCompra(),
                jpaEntity.isMeiaEntradaAplicada(),
                promocaoIdDominio,
                jpaEntity.getCodigoValidacao(),
                jpaEntity.isValidadoNaEntrada()
        );
    }

    public static List<Ingresso> toDomainEntityList(List<IngressoJpa> jpaEntityList) {
        if (jpaEntityList == null) {
            return Collections.emptyList();
        }
        return jpaEntityList.stream()
                .map(IngressoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}