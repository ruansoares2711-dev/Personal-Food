package com.pf.PersonalFood.config;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChefBotConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Bean
    public Client geminiClient() {
        // Validação simples para te ajudar no debug
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${GEMINI_API_KEY}")) {
            throw new RuntimeException("ERRO: A API Key do Gemini não foi encontrada no arquivo .env ou nas variáveis de ambiente!");
        }

        return Client.builder()
                .apiKey(apiKey) 
                .build();
    }
}