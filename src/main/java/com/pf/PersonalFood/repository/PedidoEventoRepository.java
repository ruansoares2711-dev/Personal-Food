package com.pf.PersonalFood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pf.PersonalFood.model.PedidoEvento;

public interface PedidoEventoRepository extends JpaRepository<PedidoEvento, Integer> {
    
    // Busca todos os pedidos relacionados a um Chefe específico (para o Pipeline do Chefe)
    List<PedidoEvento> findByChefeId(Integer chefeId);
    
    // Busca todos os pedidos de um Cliente (para o Painel do Cliente que criaremos depois)
    List<PedidoEvento> findByClienteId(Integer clienteId);
}