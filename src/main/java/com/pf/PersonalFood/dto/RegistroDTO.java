package com.pf.PersonalFood.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegistroDTO {
    private String nome;
    private String email;
    private String cpf;
    private String senha;

    @JsonProperty("isChef")
    private boolean isChef; // Vem do seu checkbox 'reg-is-chef'
    
    // Campos exclusivos de chef
    private String especialidade;
    private String bio;
}