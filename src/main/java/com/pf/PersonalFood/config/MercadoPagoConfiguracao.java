package com.pf.PersonalFood.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.mercadopago.MercadoPagoConfig;

import jakarta.annotation.PostConstruct;

@Configuration
public class MercadoPagoConfiguracao {

    // Puxa o token que colocamos no application.properties
    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        // Assim que o Spring Boot iniciar, ele configura a chave do Mercado Pago
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}