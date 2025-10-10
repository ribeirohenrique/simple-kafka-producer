package com.kafka.producer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.producer.model.Pagamento;
import com.kafka.producer.service.PagamentoProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PagamentoController {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoController.class);

    private final PagamentoProducerService producerService;
    private final ObjectMapper objectMapper;

    public PagamentoController(PagamentoProducerService producerService, ObjectMapper objectMapper) {
        this.producerService = producerService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarMensagem() {

        Pagamento pagamento = new Pagamento();
        pagamento.setId("1");
        pagamento.setDestinatario("jose maria");
        pagamento.setValor(2.89);
        pagamento.setTimestamp(System.currentTimeMillis());
        producerService.enviarMensagem(pagamento);
        logger.info("Mensagem enviada com sucesso");

        return ResponseEntity.ok("sucesso");
    }
}