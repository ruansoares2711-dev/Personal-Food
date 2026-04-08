package com.pf.PersonalFood.config;

import com.pf.PersonalFood.model.Chefe;
import com.pf.PersonalFood.repository.ChefeRepository; // Importe o repositório de CHEFES agora
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.util.function.Function;
import java.util.List; // Import necessário para a lista de chefs

@Configuration
public class ChefBotConfig {

    // 1. Mude para ChefeRepository aqui
    private final ChefeRepository chefeRepository;

    // 2. O Spring injeta o repositório de chefs agora
    public ChefBotConfig(ChefeRepository chefeRepository) {
        this.chefeRepository = chefeRepository;
    }

    public record PedidoRequest(String tipoServico, int convidados) {}

    @Bean
    @Description("Busca informações de chefs no banco de dados por especialidade")
    public Function<PedidoRequest, String> processarPedido() {
        return request -> {
            // 3. Agora a variável 'chefs' é criada recebendo o resultado do banco
            List<Chefe> chefs = chefeRepository.findByAtivoVitrineTrueAndEspecialidadeContainingIgnoreCase(request.tipoServico());

            // 4. O 'isEmpty' agora funciona porque 'chefs' é uma lista válida
            if (chefs.isEmpty()) {
                return "Não encontrei chefs de " + request.tipoServico() + " disponíveis no momento.";
            }

            StringBuilder contexto = new StringBuilder("Encontrei os seguintes chefs no banco de dados da Personal Food:\n");
            
            // 5. O loop agora reconhece 'chefs'
            for (Chefe c : chefs) {
                contexto.append("- Chef: ").append(c.getUsuario().getNome())
                       .append(", Especialidade: ").append(c.getEspecialidade())
                       .append(", Avaliação: ").append(c.getNotaReputacao())
                       .append(", Apresentação: ").append(c.getApresentacao()).append("\n");
            }

            return contexto.toString(); 
        };
    }
}