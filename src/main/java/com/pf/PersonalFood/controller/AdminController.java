package com.pf.PersonalFood.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pf.PersonalFood.model.TipoUsuario;
import com.pf.PersonalFood.model.Usuario;
import com.pf.PersonalFood.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    // 1. Rota para listar apenas quem está com status PENDENTE_CHEFE
    @GetMapping("/usuarios-pendentes")
    public ResponseEntity<List<Usuario>> listarPendentes() {
        // Agora filtramos especificamente quem está na fila de aprovação
        return ResponseEntity.ok(usuarioRepo.findByTipo(TipoUsuario.PENDENTE_CHEFE));
    }

    // 2. Rota para APROVAR (Trocar PENDENTE_CHEFE para CHEFE)
    @PutMapping("/aprovar-chefe/{id}")
    public ResponseEntity<String> aprovarChefe(@PathVariable Integer id) {
        return usuarioRepo.findById(id).map(usuario -> {
            // Atualiza o status para CHEFE, permitindo o login nas próximas tentativas
            usuario.setTipo(TipoUsuario.CHEFE); 
            usuarioRepo.save(usuario);          
            return ResponseEntity.ok("Usuário " + usuario.getNome() + " aprovado! Agora ele tem acesso às funções de Chefe.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. Rota para DELETAR/RECUSAR
    @DeleteMapping("/recusar-usuario/{id}")
    public ResponseEntity<String> recusarUsuario(@PathVariable Integer id) {
        if (usuarioRepo.existsById(id)) {
            // Ao recusar, deletamos o usuário ou poderíamos apenas mudar o tipo para CLIENTE
            // Como ele preencheu dados de bio/chef, o delete limpa a solicitação por completo.
            usuarioRepo.deleteById(id);
            return ResponseEntity.ok("Solicitação recusada e usuário removido.");
        }
        return ResponseEntity.notFound().build();
    }
}