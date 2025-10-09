package com.kafka.producer.service;

import com.kafka.producer.model.Pagamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PagamentoProducerService {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoProducerService.class);
    private final KafkaTemplate<String, Pagamento> kafkaTemplate;
    @Value("${app.kafka.topic}")
    private String topic;

    public PagamentoProducerService(KafkaTemplate<String, Pagamento> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarMensagem(Pagamento pagamento) {
        CompletableFuture<SendResult<String, Pagamento>> cFuture = kafkaTemplate.send(topic, pagamento);
        cFuture.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent event:[{}] with offset:[{}]", pagamento, result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send event:[{}] due to:{}", pagamento, ex.getMessage());
                throw new RuntimeException("Error ao enviar mensagem");
            }
        });
    }
}