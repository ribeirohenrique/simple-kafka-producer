package com.kafka.producer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.producer.model.Pagamento;
import com.kafka.producer.service.PagamentoProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PagamentoController {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoController.class);

    private final PagamentoProducerService producerService;
    private final ObjectMapper objectMapper;

    public PagamentoController(PagamentoProducerService producerService, ObjectMapper objectMapper) {
        this.producerService = producerService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/enviar")
    public String enviarMensagem(@RequestParam("mensagemJson") String mensagemJson, RedirectAttributes redirectAttributes) {
        try {
            // Converte a string JSON para o nosso objeto Pagamento (gerado pelo Avro)
            Pagamento pagamento = objectMapper.readValue(mensagemJson, Pagamento.class);

            // Adiciona o timestamp atual
            pagamento.setTimestamp(System.currentTimeMillis());

            // Envia para o serviço
            producerService.enviarMensagem(pagamento);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao processar JSON: " + e.getMessage());
            logger.error("Falha ao processar JSON do formulário: {}", mensagemJson, e);
        } finally {
            redirectAttributes.addFlashAttribute("successMessage", "Mensagem enviada com sucesso!");
            logger.info("JSON recebido e processado com sucesso.");
        }
        return "redirect:/";
    }
}