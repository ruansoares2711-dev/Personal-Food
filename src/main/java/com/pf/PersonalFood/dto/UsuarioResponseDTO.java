package com.pf.PersonalFood.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Integer id;
    private String nome;
    private String email;
    private String tipo;
    private String fotoPerfil;
}