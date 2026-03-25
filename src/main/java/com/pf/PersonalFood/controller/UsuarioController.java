package com.pf.PersonalFood.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

                // Usamos o método auxiliar para converter
                return ResponseEntity.ok(converterParaDTO(usuario));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Integer id, @RequestBody Map<String, Object> dados) {
        return usuarioRepo.findById(id).map(usuario -> {
            // Atualiza os campos
            if (dados.containsKey("nome")) {
                usuario.setNome((String) dados.get("nome"));
            }
            if (dados.get("fotoPerfil") != null) {
                usuario.setFotoPerfil((String) dados.get("fotoPerfil"));
            }
            
            Usuario usuarioAtualizado = usuarioRepo.save(usuario);
            
            // RETORNO CRUCIAL: Retornamos o DTO e não a Entidade Usuario diretamente
            return ResponseEntity.ok(converterParaDTO(usuarioAtualizado));
            
        }).orElse(ResponseEntity.notFound().build());
    }

    // Método auxiliar para evitar repetição de código e erros de conversão
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