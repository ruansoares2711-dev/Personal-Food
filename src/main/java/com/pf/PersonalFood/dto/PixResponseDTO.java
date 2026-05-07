package com.pf.PersonalFood.dto;

public class PixResponseDTO {
    private Long idPagamento; // ID gerado pelo Mercado Pago
    private String qrCodeBase64; // A imagem do QR Code
    private String qrCodeCopiaECola; // O texto gigante do PIX

    // Getters e Setters
    public Long getIdPagamento() { return idPagamento; }
    public void setIdPagamento(Long idPagamento) { this.idPagamento = idPagamento; }

    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }

    public String getQrCodeCopiaECola() { return qrCodeCopiaECola; }
    public void setQrCodeCopiaECola(String qrCodeCopiaECola) { this.qrCodeCopiaECola = qrCodeCopiaECola; }
}