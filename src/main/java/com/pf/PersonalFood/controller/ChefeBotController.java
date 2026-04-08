package com.pf.PersonalFood.controller;

import org.springframework.ai.chat.ChatClient; // Na 0.8.1 é esse o pacote
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bot")
public class ChefeBotController {

    private final ChatClient chatClient;

    @Autowired
    public ChefeBotController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/pergunta")
    public String perguntar(@RequestParam(value = "mensagem", defaultValue = "Dê uma dica de culinária saudável") String mensagem) {
        try {
            return chatClient.call(mensagem); 
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }
}