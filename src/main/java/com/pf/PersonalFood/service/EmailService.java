package com.pf.PersonalFood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void enviarLinkRecuperacao(String para, String token) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(para);
        mensagem.setSubject("PersonalFood - Confirmar Troca de Senha");
        
        String link = baseUrl + "/sections/reset-password.html?token=" + token;
        
        mensagem.setText("Olá!\n\nRecebemos uma solicitação para trocar sua senha.\n" +
                         "Clique no link abaixo para confirmar e escolher sua nova senha:\n\n" +
                         link + "\n\nEste link expira em 30 minutos.\n" +
                         "Se não foi você, ignore este e-mail.");
        
        mailSender.send(mensagem);
    }
}