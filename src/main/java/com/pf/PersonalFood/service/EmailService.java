package com.pf.PersonalFood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarLinkRecuperacao(String para, String token) {
    SimpleMailMessage mensagem = new SimpleMailMessage();
    mensagem.setTo(para);
    mensagem.setSubject("PersonalFood - Confirmar Troca de Senha");
    
    // O link aponta para uma nova página que vamos criar
    String link = "http://localhost:8080/sections/reset-password.html?token=" + token;
    
    mensagem.setText("Olá!\n\nRecebemos uma solicitação para trocar sua senha.\n" +
                     "Clique no link abaixo para confirmar e escolher sua nova senha:\n\n" +
                     link + "\n\nEste link expira em 30 minutos.\n" +
                     "Se não foi você, ignore este e-mail.");
    
    mailSender.send(mensagem);
}
}