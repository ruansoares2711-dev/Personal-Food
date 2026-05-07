package com.pf.PersonalFood.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import com.pf.PersonalFood.dto.PixResponseDTO;
import com.pf.PersonalFood.model.PedidoEvento;
import com.pf.PersonalFood.repository.PedidoEventoRepository;

@Service
public class PagamentoService {

    @Autowired
    private PedidoEventoRepository pedidoRepo;

    // Injeta o token vindo do application.properties
    @Value("${mercadopago.access-token}")
    private String accessToken;

    public PixResponseDTO gerarPagamentoPix(Integer pedidoId) throws Exception {
        // Configura o SDK com o token injetado
        MercadoPagoConfig.setAccessToken(accessToken);

        Optional<PedidoEvento> pedidoOpt = pedidoRepo.findById(pedidoId);
        if (pedidoOpt.isEmpty()) throw new Exception("Pedido não encontrado.");
        
        PedidoEvento pedido = pedidoOpt.get();

        // Força o token na requisição via RequestOptions
        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .accessToken(accessToken)
                .build();

        IdentificationRequest identification = IdentificationRequest.builder()
                .type("CPF").number("19119119100").build();

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(pedido.getCliente().getEmail() != null ? pedido.getCliente().getEmail() : "test_user_99@testuser.com")
                .firstName(pedido.getCliente().getNome())
                .lastName("Silva")
                .identification(identification)
                .build();

        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(pedido.getValorOrcamento())
                .description("Personal Food #" + pedido.getId())
                .paymentMethodId("pix")
                .payer(payer)
                .build();

        PaymentClient client = new PaymentClient();

        try {
            Payment payment = client.create(createRequest, requestOptions);

            PixResponseDTO response = new PixResponseDTO();
            response.setIdPagamento(payment.getId());
            response.setQrCodeCopiaECola(payment.getPointOfInteraction().getTransactionData().getQrCode());
            response.setQrCodeBase64(payment.getPointOfInteraction().getTransactionData().getQrCodeBase64());

            return response;
        } catch (com.mercadopago.exceptions.MPApiException apiEx) {
            System.err.println("ERRO MP: " + apiEx.getApiResponse().getContent());
            throw new Exception("Erro MP: " + apiEx.getApiResponse().getContent());
        }
    }

    public String consultarStatus(Long idPagamentoMP) throws Exception {
        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .accessToken(accessToken)
                .build();

        PaymentClient client = new PaymentClient();
        try {
            Payment payment = client.get(idPagamentoMP, requestOptions);
            return payment.getStatus();
        } catch (Exception e) {
            throw new Exception("Erro ao consultar status: " + e.getMessage());
        }
    }

    public PixResponseDTO gerarPagamentoCartao(Integer pedidoId, String tokenCartao, Integer parcelas, String email) throws Exception {
        MercadoPagoConfig.setAccessToken(accessToken);
        
        Optional<PedidoEvento> pedidoOpt = pedidoRepo.findById(pedidoId);
        if (pedidoOpt.isEmpty()) throw new Exception("Pedido não encontrado.");
        PedidoEvento pedido = pedidoOpt.get();

        PaymentClient client = new PaymentClient();

        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(pedido.getValorOrcamento())
                .token(tokenCartao)
                .description("Personal Food - Cartão #" + pedido.getId())
                .installments(parcelas)
                .paymentMethodId("master") // Para teste, fixamos master ou deixamos o MP detectar
                .payer(PaymentPayerRequest.builder()
                    .email(email)
                    .identification(IdentificationRequest.builder().type("CPF").number("19119119100").build())
                    .build())
                .build();

        try {
            Payment payment = client.create(createRequest);
            
            PixResponseDTO response = new PixResponseDTO();
            response.setIdPagamento(payment.getId());
            // No cartão, o status geralmente já vem como 'approved' direto
            return response;
        } catch (MPApiException apiEx) {
            System.err.println("ERRO CARTÃO: " + apiEx.getApiResponse().getContent());
            throw new Exception("Erro no cartão: " + apiEx.getApiResponse().getContent());
        }
    }
}