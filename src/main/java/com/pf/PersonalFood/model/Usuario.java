package com.pf.PersonalFood.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data // Anotação do Lombok que cria todos os Getters e Setters para você!
@Entity
@Table(name = "usuarios") // Diz qual é o nome exato da tabela no banco
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // No seu SQL está como INT

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING) // Diz para o Spring salvar o texto do Enum (ex: "CLIENTE") e não um número
    @Column(nullable = false)
    private TipoUsuario tipo;

    // O banco já coloca a data padrão, então dizemos pro Spring não tentar inserir isso
    @Column(name = "criado_em", insertable = false, updatable = false)
    private LocalDateTime criadoEm; 
}