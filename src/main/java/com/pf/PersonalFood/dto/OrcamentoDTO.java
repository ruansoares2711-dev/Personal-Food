package com.pf.PersonalFood.dto;

import java.math.BigDecimal;

public class OrcamentoDTO {
    private String propostaOrcamento;
    private BigDecimal valorOrcamento;
    private String status;

    public String getPropostaOrcamento() { return propostaOrcamento; }
    public void setPropostaOrcamento(String propostaOrcamento) { this.propostaOrcamento = propostaOrcamento; }

    public BigDecimal getValorOrcamento() { return valorOrcamento; }
    public void setValorOrcamento(BigDecimal valorOrcamento) { this.valorOrcamento = valorOrcamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}