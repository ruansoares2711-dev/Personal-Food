package com.pf.PersonalFood.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pf.PersonalFood.model.LogAprovacao;

public interface LogAprovacaoRepository extends JpaRepository<LogAprovacao, Integer> {
    
    long countByAcao(String acao);
    
}