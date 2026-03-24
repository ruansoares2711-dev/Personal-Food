package com.pf.PersonalFood.repository;

import java.util.List; // Importação que faltava
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pf.PersonalFood.model.TipoUsuario;
import com.pf.PersonalFood.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Busca um usuário pelo e-mail (usado no Login)
    Optional<Usuario> findByEmail(String email);
    
    // Busca todos os usuários por tipo (usado pelo Admin para listar Clientes ou Chefes)
    List<Usuario> findByTipo(TipoUsuario tipo);
}