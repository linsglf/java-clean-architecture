package com.cinetech.api.aplicacao;

import com.cinetech.api.dominio.enums.PerfilCliente;
import com.cinetech.api.dominio.enums.StatusSessao;
import com.cinetech.api.dominio.modelos.cliente.Cliente;
import com.cinetech.api.dominio.modelos.cliente.ClienteId;
import com.cinetech.api.dominio.modelos.credito.CreditoCompensacao;
import com.cinetech.api.dominio.modelos.ingresso.Ingresso;
import com.cinetech.api.dominio.modelos.sessao.Sessao;
import com.cinetech.api.dominio.modelos.sessao.SessaoId;
import com.cinetech.api.dominio.repositorios.ClienteRepositorio;
import com.cinetech.api.dominio.repositorios.IngressoRepositorio;
import com.cinetech.api.dominio.repositorios.SessaoRepositorio;
import com.cinetech.api.dominio.servicos.GestaoPontosFidelidadeServico.GestaoPontosFidelidadeServico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClienteAplicacao {

    private final ClienteRepositorio clienteRepositorio;
    private final IngressoRepositorio ingressoRepositorio;
    private final SessaoRepositorio sessaoRepositorio;
    private final GestaoPontosFidelidadeServico gestaoPontosFidelidadeService;

    public ClienteAplicacao(ClienteRepositorio clienteRepositorio,
                            IngressoRepositorio ingressoRepositorio,
                            SessaoRepositorio sessaoRepositorio,
                            GestaoPontosFidelidadeServico gestaoPontosFidelidadeService) {
        this.clienteRepositorio = clienteRepositorio;
        this.ingressoRepositorio = ingressoRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
        this.gestaoPontosFidelidadeService = gestaoPontosFidelidadeService;
    }

    @Transactional
    public Cliente cadastrarNovoCliente(String nome, String email, String cpf, PerfilCliente perfil) {
        if (clienteRepositorio.buscarPorCpf(cpf).isPresent()) {
            throw new IllegalArgumentException("CPF " + cpf + " já cadastrado.");
        }
        if (clienteRepositorio.buscarPorEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email " + email + " já cadastrado.");
        }
        Cliente novoCliente = new Cliente(nome, email, cpf, perfil);
        return clienteRepositorio.salvar(novoCliente);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorId(ClienteId clienteId) {
        Objects.requireNonNull(clienteId, "ID do Cliente não pode ser nulo.");
        return clienteRepositorio.buscarPorId(clienteId);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio para busca.");
        }
        return clienteRepositorio.buscarPorCpf(cpf);
    }

    @Transactional
    public void emitirCreditosParaSessaoCancelada(SessaoId sessaoIdCancelada) {
        Objects.requireNonNull(sessaoIdCancelada, "ID da Sessão cancelada não pode ser nulo.");
        Sessao sessaoCancelada = sessaoRepositorio.buscarPorId(sessaoIdCancelada)
                .orElseThrow(() -> new IllegalArgumentException("Sessão com ID " + sessaoIdCancelada + " não encontrada."));

        if (sessaoCancelada.getStatus() != StatusSessao.CANCELADA) {
            throw new IllegalStateException("A sessão " + sessaoIdCancelada + " não está com status CANCELADA. Status atual: " + sessaoCancelada.getStatus());
        }

        List<Ingresso> ingressosDaSessao = ingressoRepositorio.buscarPorSessaoId(sessaoIdCancelada);
        if (ingressosDaSessao.isEmpty()) {
            System.out.println("INFO APP: Nenhum ingresso encontrado para a sessão cancelada " + sessaoIdCancelada + ". Nenhum crédito a emitir.");
            return;
        }

        for (Ingresso ingresso : ingressosDaSessao) {
            // Carrega o cliente para garantir que estamos trabalhando com a instância mais recente
            // e para que as alterações em sua lista de créditos sejam persistidas corretamente.
            Cliente clienteDoIngresso = clienteRepositorio.buscarPorId(ingresso.getCliente().getId())
                    .orElseThrow(() -> new IllegalStateException("Cliente do ingresso " + ingresso.getId() + " não encontrado."));

            BigDecimal valorCredito = ingresso.getValorPago();
            LocalDateTime dataValidadeCredito = LocalDateTime.now().plusYears(1); // Ex: validade de 1 ano
            String motivo = "Crédito por cancelamento da sessão ID: " + sessaoCancelada.getId() +
                    " (Filme: " + sessaoCancelada.getFilme().getTitulo() +
                    ", Horário: " + sessaoCancelada.getDataHoraInicio() + ")";

            CreditoCompensacao novoCredito = new CreditoCompensacao(
                    clienteDoIngresso.getId(),
                    valorCredito,
                    dataValidadeCredito,
                    motivo,
                    sessaoCancelada.getId()
            );
            clienteDoIngresso.adicionarCreditoCompensacao(novoCredito); // Método na entidade Cliente
            clienteRepositorio.salvar(clienteDoIngresso); // Salva o cliente com o novo crédito

            System.out.println("INFO APP: Crédito de " + valorCredito + " emitido para cliente " + clienteDoIngresso.getId() +
                    " devido ao cancelamento da sessão " + sessaoIdCancelada);
            // Aqui poderia haver uma notificação ao cliente
        }
    }

    @Transactional
    public void processarExpiracaoPontosFidelidadeDeTodosClientes() {
        LocalDate hoje = LocalDate.now();
        System.out.println("INFO APP: Iniciando rotina de expiração de pontos de fidelidade em " + hoje);

        List<Cliente> todosClientes = clienteRepositorio.buscarTodos();
        int totalClientesComPontosExpirados = 0;

        for (Cliente cliente : todosClientes) {
            int saldoAntes = cliente.getSaldoTotalPontosFidelidadeValidos(hoje);
            gestaoPontosFidelidadeService.expirarPontosObsoletosParaCliente(cliente, hoje);
            int saldoDepois = cliente.getSaldoTotalPontosFidelidadeValidos(hoje);

            if (saldoAntes != saldoDepois) {
                clienteRepositorio.salvar(cliente);
                System.out.println("INFO APP: Pontos expirados para cliente " + cliente.getId() + ". Saldo anterior: " + saldoAntes + ", Saldo atual: " + saldoDepois);
                totalClientesComPontosExpirados++;
            }
        }
        System.out.println("INFO APP: Rotina de expiração de pontos concluída. " + totalClientesComPontosExpirados + " clientes tiveram pontos expirados.");
    }
}
