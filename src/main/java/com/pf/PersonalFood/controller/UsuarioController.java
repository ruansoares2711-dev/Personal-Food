package com.pf.PersonalFood.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pf.PersonalFood.dto.LoginDTO;
import com.pf.PersonalFood.dto.RegistroDTO;
import com.pf.PersonalFood.dto.UsuarioResponseDTO;
import com.pf.PersonalFood.model.Chefe;
import com.pf.PersonalFood.model.TipoUsuario;
import com.pf.PersonalFood.model.Usuario;
import com.pf.PersonalFood.repository.ChefeRepository;
import com.pf.PersonalFood.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ChefeRepository chefeRepo;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody RegistroDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setCpf(dto.getCpf());
        novoUsuario.setSenha(dto.getSenha());
        
        // 👇 AQUI ESTÁ A MUDANÇA:
        // Se a checkbox (isChef) vier true, o status é PENDENTE_CHEFE.
        // Se vier false, ele vira CLIENTE direto.
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
                
                // BLOQUEIO: Se o usuário ainda estiver pendente, não deixa logar.
                if (usuario.getTipo() == TipoUsuario.PENDENTE_CHEFE) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                         .body("Aguardando solicitação: Seu perfil de Chefe ainda não foi aprovado.");
                }

                UsuarioResponseDTO response = new UsuarioResponseDTO();
                response.setId(usuario.getId());
                response.setNome(usuario.getNome());
                response.setEmail(usuario.getEmail());
                response.setTipo(usuario.getTipo().name());
                
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
    }
}