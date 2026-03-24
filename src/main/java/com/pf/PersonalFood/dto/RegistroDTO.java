package com.pf.PersonalFood.dto;

import lombok.Data;

@Data
public class RegistroDTO {
    private String nome;
    private String email;
    private String cpf;
    private String senha;
    private boolean isChef; // Vem do seu checkbox 'reg-is-chef'
    
    // Campos exclusivos de chef
    private String especialidade;
    private String bio;
}