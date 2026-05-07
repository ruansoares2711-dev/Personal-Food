package com.pf.PersonalFood.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pf.PersonalFood.model.LogAprovacao;
import com.pf.PersonalFood.model.TipoUsuario;
import com.pf.PersonalFood.model.Usuario;
import com.pf.PersonalFood.repository.LogAprovacaoRepository;
import com.pf.PersonalFood.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private LogAprovacaoRepository logRepo;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios-pendentes")
    public ResponseEntity<List<Usuario>> listarPendentes() {
        return ResponseEntity.ok(usuarioRepo.findByTipo(TipoUsuario.PENDENTE_CHEFE));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/metricas-dashboard")
    public ResponseEntity<Map<String, Long>> obterMetricas() {
        Map<String, Long> metricas = new HashMap<>();
        metricas.put("aprovados", logRepo.countByAcao("APROVADO"));
        metricas.put("recusados", logRepo.countByAcao("RECUSADO"));
        return ResponseEntity.ok(metricas);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/aprovar-chefe/{id}")
    public ResponseEntity<String> aprovarChefe(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        
        usuario.setTipo(TipoUsuario.CHEFE);
        usuarioRepo.save(usuario);
        
        LogAprovacao log = new LogAprovacao();
        log.setAdminNome(userDetails.getUsername());
        log.setCandidatoNome(usuario.getNome());
        log.setAcao("APROVADO");
        logRepo.save(log);

        return ResponseEntity.ok("Usuário aprovado com sucesso!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/recusar-usuario/{id}")
    public ResponseEntity<String> recusarUsuario(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        
        String nomeCandidato = usuario.getNome();
        usuarioRepo.deleteById(id);

        LogAprovacao log = new LogAprovacao();
        log.setAdminNome(userDetails.getUsername());
        log.setCandidatoNome(nomeCandidato);
        log.setAcao("RECUSADO");
        logRepo.save(log);

        return ResponseEntity.ok("Solicitação recusada e removida.");
    }
}