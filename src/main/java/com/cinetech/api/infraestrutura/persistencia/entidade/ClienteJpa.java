package com.cinetech.api.infraestrutura.persistencia.entidade;

import com.cinetech.api.dominio.enums.PerfilCliente;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "cliente")
public class ClienteJpa {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 11) // Apenas números
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PerfilCliente perfil;

    // Um cliente pode ter muitos créditos de compensação.
    // Se ClienteJpa é o "dono" do relacionamento, cascade e orphanRemoval fazem sentido.
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CreditoCompensacaoJpa> creditosCompensacaoJpa = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PontoFidelidadeJpa> pontosFidelidadeJpa = new ArrayList<>();

    public ClienteJpa() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public PerfilCliente getPerfil() { return perfil; }
    public void setPerfil(PerfilCliente perfil) { this.perfil = perfil; }
    public List<CreditoCompensacaoJpa> getCreditosCompensacaoJpa() { return creditosCompensacaoJpa; }
    public void setCreditosCompensacaoJpa(List<CreditoCompensacaoJpa> creditosCompensacaoJpa) { this.creditosCompensacaoJpa = creditosCompensacaoJpa; }
    public List<PontoFidelidadeJpa> getPontosFidelidadeJpa() { return pontosFidelidadeJpa; }
    public void setPontosFidelidadeJpa(List<PontoFidelidadeJpa> pontosFidelidadeJpa) { this.pontosFidelidadeJpa = pontosFidelidadeJpa; }

    // Métodos utilitários para gerenciar relacionamentos bidirecionais (se aplicável)
    public void adicionarCreditoCompensacaoJpa(CreditoCompensacaoJpa credito) {
        creditosCompensacaoJpa.add(credito);
        credito.setCliente(this);
    }
    public void removerCreditoCompensacaoJpa(CreditoCompensacaoJpa credito) {
        creditosCompensacaoJpa.remove(credito);
        credito.setCliente(null);
    }
    public void adicionarPontoFidelidadeJpa(PontoFidelidadeJpa ponto) {
        pontosFidelidadeJpa.add(ponto);
        ponto.setCliente(this);
    }
    public void removerPontoFidelidadeJpa(PontoFidelidadeJpa ponto) {
        pontosFidelidadeJpa.remove(ponto);
        ponto.setCliente(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteJpa that = (ClienteJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
