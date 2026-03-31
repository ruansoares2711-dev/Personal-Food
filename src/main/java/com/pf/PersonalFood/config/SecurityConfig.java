package com.pf.PersonalFood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", 
                    "/sections/**", 
                    "/assets/**", 
                    "/api/pagamentos/**",
                    "/api/usuarios/login", 
                    "/api/usuarios/registrar", 
                    "/api/usuarios/recuperar-senha", // Rota da recuperação de senha
                    "/sections/reset-password.html"
                ).permitAll() 
                
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/sections/painel-cliente.html", true)
            );
        
        return http.build();
    }
}