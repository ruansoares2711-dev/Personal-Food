package com.pf.PersonalFood.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public boolean isChef() { return isChef; }
    public void setChef(boolean isChef) { this.isChef = isChef; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}