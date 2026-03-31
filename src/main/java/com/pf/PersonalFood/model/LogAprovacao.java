package com.pf.PersonalFood.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "logs_aprovacao")
public class LogAprovacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "admin_nome")
    private String adminNome;

    @Column(name = "candidato_nome")
    private String candidatoNome;

    private String acao;

    @Column(name = "data_acao")
    private LocalDateTime dataAcao;

    @PrePersist
    protected void onCreate() {
        this.dataAcao = LocalDateTime.now(); // Preenche a data automaticamente
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getAdminNome() { return adminNome; }
    public void setAdminNome(String adminNome) { this.adminNome = adminNome; }

    public String getCandidatoNome() { return candidatoNome; }
    public void setCandidatoNome(String candidatoNome) { this.candidatoNome = candidatoNome; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public LocalDateTime getDataAcao() { return dataAcao; }
    public void setDataAcao(LocalDateTime dataAcao) { this.dataAcao = dataAcao; }
}