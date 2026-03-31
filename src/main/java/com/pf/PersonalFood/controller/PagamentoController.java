package com.pf.PersonalFood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pf.PersonalFood.dto.PixResponseDTO;
import com.pf.PersonalFood.service.PagamentoService;

@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/pix/{pedidoId}")
    public ResponseEntity<?> gerarPixParaPedido(@PathVariable Integer pedidoId) {
        try {
            PixResponseDTO pix = pagamentoService.gerarPagamentoPix(pedidoId);
            return ResponseEntity.ok(pix);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao gerar PIX: " + e.getMessage());
        }
    }

    // ADICIONE ESTE MÉTODO ABAIXO PARA RESOLVER O 404:
    @GetMapping("/status/{idPagamentoMP}")
    public ResponseEntity<String> consultarStatusPagamento(@PathVariable Long idPagamentoMP) {
        try {
            // Esse método chama o service que consulta o Mercado Pago
            String status = pagamentoService.consultarStatus(idPagamentoMP);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao consultar status");
        }
    }
}