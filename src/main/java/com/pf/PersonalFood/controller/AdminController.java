package com.pf.PersonalFood.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pf.PersonalFood.model.LogAprovacao;
import com.pf.PersonalFood.model.TipoUsuario;
import com.pf.PersonalFood.model.Usuario;
import com.pf.PersonalFood.repository.LogAprovacaoRepository;
import com.pf.PersonalFood.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private LogAprovacaoRepository logRepo;

    @GetMapping("/usuarios-pendentes")
    public ResponseEntity<List<Usuario>> listarPendentes() {
        return ResponseEntity.ok(usuarioRepo.findByTipo(TipoUsuario.PENDENTE_CHEFE));
    }

    // NOVA ROTA: Retorna as métricas para o Dashboard
    @GetMapping("/metricas-dashboard")
    public ResponseEntity<Map<String, Long>> obterMetricas() {
        Map<String, Long> metricas = new HashMap<>();
        metricas.put("aprovados", logRepo.countByAcao("APROVADO"));
        metricas.put("recusados", logRepo.countByAcao("RECUSADO"));
        return ResponseEntity.ok(metricas);
    }

    // ATUALIZADO: Recebe o nome do Admin via parâmetro para salvar no Log
    @PutMapping("/aprovar-chefe/{id}")
    public ResponseEntity<String> aprovarChefe(@PathVariable Integer id, @RequestParam String adminNome) {
        return usuarioRepo.findById(id).map(usuario -> {
            usuario.setTipo(TipoUsuario.CHEFE); 
            usuarioRepo.save(usuario);          
            
            // Salva no Histórico
            LogAprovacao log = new LogAprovacao();
            log.setAdminNome(adminNome);
            log.setCandidatoNome(usuario.getNome());
            log.setAcao("APROVADO");
            logRepo.save(log);

            return ResponseEntity.ok("Usuário aprovado com sucesso!");
        }).orElse(ResponseEntity.notFound().build());
    }

    // ATUALIZADO: Recebe o nome do Admin via parâmetro para salvar no Log
    @DeleteMapping("/recusar-usuario/{id}")
    public ResponseEntity<String> recusarUsuario(@PathVariable Integer id, @RequestParam String adminNome) {
        return usuarioRepo.findById(id).map(usuario -> {
            String nomeCandidato = usuario.getNome();
            
            // Deleta o usuário recusado
            usuarioRepo.deleteById(id);

            // Salva no Histórico
            LogAprovacao log = new LogAprovacao();
            log.setAdminNome(adminNome);
            log.setCandidatoNome(nomeCandidato);
            log.setAcao("RECUSADO");
            logRepo.save(log);

            return ResponseEntity.ok("Solicitação recusada e removida.");
        }).orElse(ResponseEntity.notFound().build());
    }
}