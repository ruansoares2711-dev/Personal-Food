package com.pf.PersonalFood.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pf.PersonalFood.model.Chefe;
import com.pf.PersonalFood.model.PedidoEvento;
import com.pf.PersonalFood.model.Usuario;
import com.pf.PersonalFood.repository.ChefeRepository;
import com.pf.PersonalFood.repository.PedidoEventoRepository;
import com.pf.PersonalFood.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoEventoRepository pedidoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ChefeRepository chefeRepo;

    @PostMapping("/novo")
    public ResponseEntity<String> criarPedido(@RequestBody Map<String, Object> dados) {
        try {
            // Extraindo os dados do JSON que veio do Front-end
            Integer clienteId = Integer.parseInt(dados.get("clienteId").toString());
            Integer chefeId = Integer.parseInt(dados.get("chefeId").toString());
            LocalDate dataEvento = LocalDate.parse(dados.get("dataEvento").toString());
            Integer qtdPessoas = Integer.parseInt(dados.get("quantidadePessoas").toString());
            String local = dados.get("localEvento").toString();
            String tipoServico = dados.get("tipoServico").toString();
            
            // Extraindo as fotos (String em formato JSON)
            String fotosCozinha = dados.get("fotosCozinha") != null ? dados.get("fotosCozinha").toString() : null;

            // Buscando os usuários no banco
            Optional<Usuario> clienteOpt = usuarioRepo.findById(clienteId);
            Optional<Chefe> chefeOpt = chefeRepo.findById(chefeId);

            if (clienteOpt.isPresent() && chefeOpt.isPresent()) {
                PedidoEvento novoPedido = new PedidoEvento();
                novoPedido.setCliente(clienteOpt.get());
                novoPedido.setChefe(chefeOpt.get());
                novoPedido.setDataEvento(dataEvento);
                novoPedido.setQuantidadePessoas(qtdPessoas);
                
                // Juntando o tipo de serviço com o endereço para caber na nossa tabela atual
                novoPedido.setLocalEvento(tipoServico + " | " + local);
                
                // Salvando as fotos da cozinha
                novoPedido.setFotosCozinha(fotosCozinha);
                
                // Status inicial para cair no Pipeline do Chef
                novoPedido.setStatus("NOVA_SOLICITACAO");

                pedidoRepo.save(novoPedido);
                return ResponseEntity.ok("Solicitação enviada com sucesso!");
            }

            return ResponseEntity.badRequest().body("Erro: Cliente ou Chefe não encontrados no sistema.");
            
        } catch (Exception e) {
            e.printStackTrace(); // Imprime o erro no console do Spring Boot para facilitar a correção
            return ResponseEntity.badRequest().body("Erro ao processar o pedido: Verifique os dados enviados.");
        }
    }

    // ==========================================
    // ROTAS DO CLIENTE
    // ==========================================

    // Rota para o Cliente ver os pedidos/orçamentos dele
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoEvento>> listarPedidosCliente(@PathVariable Integer clienteId) {
        List<PedidoEvento> pedidos = pedidoRepo.findByClienteId(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    // Rota para o Cliente Aceitar ou Recusar o orçamento do Chef
    @PutMapping("/{pedidoId}/responder")
    public ResponseEntity<String> responderOrcamento(@PathVariable Integer pedidoId, @RequestBody Map<String, String> dados) {
        Optional<PedidoEvento> pedidoOpt = pedidoRepo.findById(pedidoId);
        
        if (pedidoOpt.isPresent()) {
            PedidoEvento pedido = pedidoOpt.get();
            String resposta = dados.get("resposta"); // Espera receber "ACEITO" ou "RECUSADO"
            
            if ("ACEITO".equals(resposta)) {
                pedido.setStatus("CONFIRMADO");
            } else {
                pedido.setStatus("RECUSADO");
            }
            
            pedidoRepo.save(pedido);
            return ResponseEntity.ok("Sua resposta foi enviada ao Chef!");
        }
        return ResponseEntity.notFound().build();
    }
}