package com.pf.PersonalFood.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse; // Import necessário para a resposta
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bot")
public class ChefeBotController {

    private final Client client;

    // O Spring injeta automaticamente o 'geminiClient' que você configurou na ChefBotConfig
    public ChefeBotController(Client client) {
        this.client = client;
    }

    @GetMapping("/pergunta")
    public String perguntar(@RequestParam(value = "mensagem", defaultValue = "") String mensagem) {
        try {
            GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", mensagem, null);
            
            return response.text();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao conectar com Gemini: " + e.getMessage();
        }
    }
}