package com.pf.PersonalFood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "chefes")
public class Chefe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relacionamento: Um Chefe está ligado a Um Usuário
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false) 
    private Usuario usuario;

    @Column(length = 100)
    private String especialidade;

    @Column(columnDefinition = "TEXT")
    private String apresentacao;

    @Column(name = "foto_perfil") // Mapeia o nome do Java para o nome da coluna no banco
    private String fotoPerfil;
}