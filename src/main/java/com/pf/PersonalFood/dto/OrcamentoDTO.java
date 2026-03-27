package com.pf.PersonalFood.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrcamentoDTO {
    private String propostaOrcamento;
    private BigDecimal valorOrcamento;
    private String status;
}