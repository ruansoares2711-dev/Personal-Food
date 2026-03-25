package com.pf.PersonalFood.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "logs_aprovacao")
@Data
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
}