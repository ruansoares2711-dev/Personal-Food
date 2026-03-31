package com.pf.PersonalFood.service;

import com.pf.PersonalFood.model.Usuario;
import com.pf.PersonalFood.model.TipoUsuario;
import com.pf.PersonalFood.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // Extrai os dados que o Google enviou
        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");
        
        // 📸 AQUI ESTÁ A MÁGICA: Capturamos a URL da foto do Google
        String fotoUrl = oAuth2User.getAttribute("picture"); 

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            // Se for o primeiro acesso, cria o usuário já com a foto
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setTipo(TipoUsuario.CLIENTE);
            novoUsuario.setSenha("LOGIN_GOOGLE"); 
            novoUsuario.setFotoPerfil(fotoUrl); // <-- Salva a URL da foto no banco
            
            usuarioRepository.save(novoUsuario);
        } else {
            // Se o usuário já existe mas por algum motivo não tem foto, 
            // a gente aproveita e atualiza a foto dele com a do Google!
            Usuario usuarioExistente = usuarioOptional.get();
            if (usuarioExistente.getFotoPerfil() == null && fotoUrl != null) {
                usuarioExistente.setFotoPerfil(fotoUrl);
                usuarioRepository.save(usuarioExistente);
            }
        }

        return oAuth2User;
    }
}