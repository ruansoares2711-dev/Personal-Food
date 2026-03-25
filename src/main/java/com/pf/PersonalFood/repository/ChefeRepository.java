package com.pf.PersonalFood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pf.PersonalFood.model.Chefe;

public interface ChefeRepository extends JpaRepository<Chefe, Integer> {
    
    // Busca os dados do Chefe usando o ID do Usuário associado a ele
    Optional<Chefe> findByUsuarioId(Integer usuarioId);
    
}