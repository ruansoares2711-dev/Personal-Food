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

@Entity
@Table(name = "chefes")
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

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getApresentacao() { return apresentacao; }
    public void setApresentacao(String apresentacao) { this.apresentacao = apresentacao; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public boolean isAtivoVitrine() { return ativoVitrine; }
    public void setAtivoVitrine(boolean ativoVitrine) { this.ativoVitrine = ativoVitrine; }

    public String getTagsAtendimento() { return tagsAtendimento; }
    public void setTagsAtendimento(String tagsAtendimento) { this.tagsAtendimento = tagsAtendimento; }

    public BigDecimal getNotaReputacao() { return notaReputacao; }
    public void setNotaReputacao(BigDecimal notaReputacao) { this.notaReputacao = notaReputacao; }

    public Integer getTotalAvaliacoes() { return totalAvaliacoes; }
    public void setTotalAvaliacoes(Integer totalAvaliacoes) { this.totalAvaliacoes = totalAvaliacoes; }
}