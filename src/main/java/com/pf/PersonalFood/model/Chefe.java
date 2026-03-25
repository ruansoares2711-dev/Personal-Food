package com.pf.PersonalFood.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "chefes")
@Data
public class Chefe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String especialidade;
    
    @Column(columnDefinition = "TEXT")
    private String apresentacao;
    
    @Column(name = "foto_perfil", columnDefinition = "LONGTEXT")
    private String fotoPerfil;

    // --- NOVOS CAMPOS DA VITRINE ---
    
    @Column(name = "ativo_vitrine")
    private boolean ativoVitrine = true; // Por padrão, o perfil nasce ativo

    @Column(name = "tags_atendimento")
    private String tagsAtendimento = "todos";

    @Column(name = "nota_reputacao")
    private BigDecimal notaReputacao = new BigDecimal("5.00"); // Começa com 5 estrelas

    @Column(name = "total_avaliacoes")
    private Integer totalAvaliacoes = 0;
}