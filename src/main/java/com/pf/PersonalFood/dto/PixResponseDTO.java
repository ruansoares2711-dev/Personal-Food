package com.pf.PersonalFood.dto;

import lombok.Data;

@Data
public class PixResponseDTO {
    private Long idPagamento; // ID gerado pelo Mercado Pago
    private String qrCodeBase64; // A imagem do QR Code
    private String qrCodeCopiaECola; // O texto gigante do PIX
}