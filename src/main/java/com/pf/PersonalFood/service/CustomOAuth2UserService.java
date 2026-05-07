package com.pf.PersonalFood.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.pf.PersonalFood.model.TipoUsuario;
import com.pf.PersonalFood.model.Usuario;
import com.pf.PersonalFood.repository.UsuarioRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Validate token issuer
        validateOAuth2Token(userRequest);
        
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String email = oAuth2User.getAttribute("email");
        Boolean emailVerified = oAuth2User.getAttribute("email_verified");
        String nome = oAuth2User.getAttribute("name");
        String fotoUrl = oAuth2User.getAttribute("picture");
        
        // Email must be verified by Google
        if (emailVerified == null || !emailVerified) {
            throw new OAuth2AuthenticationException(new OAuth2Error("unverified_email"), 
                "Email not verified by Google");
        }
        
        // Sanitize URL to prevent XSS
        if (fotoUrl != null && !fotoUrl.startsWith("https://")) {
            fotoUrl = null;
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setTipo(TipoUsuario.CLIENTE);
            novoUsuario.setSenha("OAUTH2_GOOGLE");
            novoUsuario.setFotoPerfil(fotoUrl);
            usuarioRepository.save(novoUsuario);
        } else {
            Usuario usuarioExistente = usuarioOptional.get();
            if (usuarioExistente.getFotoPerfil() == null && fotoUrl != null) {
                usuarioExistente.setFotoPerfil(fotoUrl);
                usuarioRepository.save(usuarioExistente);
            }
        }

        return oAuth2User;
    }

    private void validateOAuth2Token(OAuth2UserRequest userRequest) {
        String issuer = (String) userRequest.getAdditionalParameters().get("iss");
        if (issuer == null || !issuer.equals("https://accounts.google.com")) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_issuer"), 
                "Invalid token issuer");
        }
    }
}