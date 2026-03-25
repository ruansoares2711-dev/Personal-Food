package com.pf.PersonalFood.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pf.PersonalFood.model.Chefe;
import com.pf.PersonalFood.model.PedidoEvento;
import com.pf.PersonalFood.repository.ChefeRepository;
import com.pf.PersonalFood.repository.PedidoEventoRepository;

@RestController
@RequestMapping("/api/chefes")
@CrossOrigin(origins = "*")
public class ChefeController {

    @Autowired
    private ChefeRepository chefeRepo;

    @Autowired
    private PedidoEventoRepository pedidoRepo;

    // ==========================================
    // ROTAS PÚBLICAS (Vitrine)
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Chefe>> listarChefes() {
        // Mantido: Retorna todos os chefes para a tela pública de busca
        List<Chefe> listaDeChefes = chefeRepo.findAll();
        return ResponseEntity.ok(listaDeChefes);
    }

    // ==========================================
    // ROTAS PRIVADAS (Painel do Chef -> Aba Perfil)
    // ==========================================

    @GetMapping("/perfil/{usuarioId}")
    public ResponseEntity<Chefe> buscarPerfil(@PathVariable Integer usuarioId) {
        Optional<Chefe> chefe = chefeRepo.findByUsuarioId(usuarioId);
        return chefe.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/perfil/{usuarioId}")
    public ResponseEntity<String> atualizarPerfil(@PathVariable Integer usuarioId, @RequestBody Map<String, Object> dados) {
        Optional<Chefe> chefeOpt = chefeRepo.findByUsuarioId(usuarioId);

        if (chefeOpt.isPresent()) {
            Chefe chefe = chefeOpt.get();
            
            // Atualiza os dados da vitrine com o que veio do front-end
            chefe.setAtivoVitrine((Boolean) dados.get("ativoVitrine"));
            chefe.setEspecialidade((String) dados.get("especialidade"));
            chefe.setTagsAtendimento((String) dados.get("tags"));
            chefe.setApresentacao((String) dados.get("bio"));

            // 👇 AQUI ESTÁ O AJUSTE PARA SALVAR A FOTO:
            if (dados.get("fotoPerfil") != null) {
                chefe.setFotoPerfil((String) dados.get("fotoPerfil"));
            }

            chefeRepo.save(chefe);
            return ResponseEntity.ok("Configurações da vitrine salvas com sucesso!");
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // ROTAS PRIVADAS (Painel do Chef -> Aba Eventos)
    // ==========================================

    @GetMapping("/{usuarioId}/pedidos")
    public ResponseEntity<List<PedidoEvento>> listarPedidos(@PathVariable Integer usuarioId) {
        Optional<Chefe> chefeOpt = chefeRepo.findByUsuarioId(usuarioId);
        
        if (chefeOpt.isPresent()) {
            // Busca todos os pedidos do Kanban vinculados a este chefe
            List<PedidoEvento> pedidos = pedidoRepo.findByChefeId(chefeOpt.get().getId());
            return ResponseEntity.ok(pedidos);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/pedidos/{pedidoId}/orcamento")
    public ResponseEntity<String> enviarOrcamento(@PathVariable Integer pedidoId, @RequestBody Map<String, Object> dados) {
        Optional<PedidoEvento> pedidoOpt = pedidoRepo.findById(pedidoId);
        
        if (pedidoOpt.isPresent()) {
            PedidoEvento pedido = pedidoOpt.get();
            
            // Converte os dados recebidos do Modal
            BigDecimal valor = new BigDecimal(dados.get("valor").toString());
            String proposta = (String) dados.get("proposta");
            
            pedido.setValorOrcamento(valor);
            pedido.setPropostaOrcamento(proposta);
            pedido.setStatus("ORCAMENTO_ENVIADO"); // Move o card no Pipeline
            
            pedidoRepo.save(pedido);
            return ResponseEntity.ok("Orçamento enviado ao cliente!");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/pedidos/{pedidoId}/recusar")
    public ResponseEntity<String> recusarPedido(@PathVariable Integer pedidoId) {
        Optional<PedidoEvento> pedidoOpt = pedidoRepo.findById(pedidoId);
        
        if (pedidoOpt.isPresent()) {
            PedidoEvento pedido = pedidoOpt.get();
            pedido.setStatus("RECUSADO");
            pedidoRepo.save(pedido);
            return ResponseEntity.ok("Evento recusado.");
        }
        return ResponseEntity.notFound().build();
    }
}