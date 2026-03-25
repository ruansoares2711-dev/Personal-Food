package com.pf.PersonalFood.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pedidos_evento")
@Data
public class PedidoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quem pediu
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    // Quem vai executar
    @ManyToOne
    @JoinColumn(name = "chefe_id", nullable = false)
    private Chefe chefe;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "quantidade_pessoas", nullable = false)
    private Integer quantidadePessoas;

    @Column(name = "local_evento", nullable = false)
    private String localEvento;

    @Column(name = "valor_orcamento")
    private BigDecimal valorOrcamento;

    @Column(name = "proposta_orcamento", columnDefinition = "LONGTEXT")
    private String propostaOrcamento;

    // 👇 AQUI ESTÁ A COLUNA NOVA QUE VOCÊ CRIOU NO BANCO
    @Column(name = "fotos_cozinha", columnDefinition = "LONGTEXT")
    private String fotosCozinha;

    // Status: NOVA_SOLICITACAO, ORCAMENTO_ENVIADO, CONFIRMADO, RECUSADO
    private String status = "NOVA_SOLICITACAO";

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }
}