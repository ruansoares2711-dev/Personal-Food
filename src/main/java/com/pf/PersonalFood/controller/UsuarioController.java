package com.pf.PersonalFood.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.userdetails.UserDetails;

import com.pf.PersonalFood.dto.LoginDTO;
import com.pf.PersonalFood.dto.RegistroDTO;
import com.pf.PersonalFood.dto.UsuarioResponseDTO;
import com.pf.PersonalFood.model.Chefe;
import com.pf.PersonalFood.model.PasswordResetToken;
import com.pf.PersonalFood.model.TipoUsuario;
import com.pf.PersonalFood.model.Usuario;
import com.pf.PersonalFood.repository.ChefeRepository;
import com.pf.PersonalFood.repository.UsuarioRepository;
import com.pf.PersonalFood.repository.PasswordResetTokenRepository;
import com.pf.PersonalFood.service.EmailService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ChefeRepository chefeRepo;

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private EmailService emailService;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody RegistroDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setCpf(dto.getCpf());
        novoUsuario.setSenha(dto.getSenha());
        
        if (dto.isChef()) {
            novoUsuario.setTipo(TipoUsuario.PENDENTE_CHEFE);
        } else {
            novoUsuario.setTipo(TipoUsuario.CLIENTE);
        }
        
        Usuario usuarioSalvo = usuarioRepo.save(novoUsuario);

        if (dto.isChef()) {
            Chefe novoChefe = new Chefe();
            novoChefe.setUsuario(usuarioSalvo);
            novoChefe.setEspecialidade(dto.getEspecialidade());
            novoChefe.setApresentacao(dto.getBio());
            chefeRepo.save(novoChefe);
            return ResponseEntity.ok("Cadastro realizado! Sua solicitação para ser Chefe está em análise.");
        }

        return ResponseEntity.ok("Cadastro de cliente realizado com sucesso!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody LoginDTO dto) {
        Optional<Usuario> usuarioBuscado = usuarioRepo.findByEmail(dto.getEmail());

        if (usuarioBuscado.isPresent()) {
            Usuario usuario = usuarioBuscado.get();
            if (usuario.getSenha().equals(dto.getSenha())) {
                if (usuario.getTipo() == TipoUsuario.PENDENTE_CHEFE) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                         .body("Aguardando solicitação: Seu perfil de Chefe ainda não foi aprovado.");
                }

                return ResponseEntity.ok(converterParaDTO(usuario));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Integer id, @RequestBody Map<String, Object> dados) {
        return usuarioRepo.findById(id).map(usuario -> {
            if (dados.containsKey("nome")) {
                usuario.setNome((String) dados.get("nome"));
            }
            if (dados.get("fotoPerfil") != null) {
                usuario.setFotoPerfil((String) dados.get("fotoPerfil"));
            }
            
            Usuario usuarioAtualizado = usuarioRepo.save(usuario);
            return ResponseEntity.ok(converterParaDTO(usuarioAtualizado));
            
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> obterUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nenhum usuário logado na sessão.");
        }

        String email = null;

        if (authentication.getPrincipal() instanceof OAuth2User) {
            email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
        } 
        else if (authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        } 
        else {
            email = authentication.getName();
        }

        Optional<Usuario> usuarioBuscado = usuarioRepo.findByEmail(email);
        if (usuarioBuscado.isPresent()) {
            return ResponseEntity.ok(converterParaDTO(usuarioBuscado.get()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado no banco.");
    }

    @PostMapping("/solicitar-recuperacao")
    public ResponseEntity<String> solicitarRecuperacao(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            System.out.println("Tentando recuperar senha para: " + email); // LOG 1

            Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("E-mail não encontrado.");
            }

            Usuario usuario = usuarioOpt.get();
            System.out.println("Usuário encontrado: " + usuario.getNome()); // LOG 2

            String token = UUID.randomUUID().toString();
            
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUsuario(usuario);
            resetToken.setDataExpiracao(LocalDateTime.now().plusMinutes(30));
            
            System.out.println("Salvando token no banco..."); // LOG 3
            tokenRepo.save(resetToken); 
            System.out.println("Token salvo com sucesso!"); // LOG 4

            emailService.enviarLinkRecuperacao(email, token);
            System.out.println("E-mail enviado!"); // LOG 5
            
            return ResponseEntity.ok("Link de confirmação enviado para o seu e-mail.");

        } catch (Exception e) {
            // ISSO VAI MOSTRAR O ERRO REAL NO CONSOLE DO SPRING BOOT
            System.err.println("ERRO NA RECUPERAÇÃO: " + e.getMessage());
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erro interno no servidor: " + e.getMessage());
        }
    }

    @PostMapping("/confirmar-nova-senha")
    public ResponseEntity<String> confirmarNovaSenha(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String novaSenha = body.get("novaSenha");

        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido.");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getDataExpiracao().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(resetToken);
            return ResponseEntity.status(HttpStatus.GONE).body("O token expirou. Solicite uma nova recuperação.");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(novaSenha);
        usuarioRepo.save(usuario);
        
        tokenRepo.delete(resetToken);

        return ResponseEntity.ok("Senha alterada com sucesso!");
    }

    private UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTipo(usuario.getTipo().name());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        return dto;
    }
}