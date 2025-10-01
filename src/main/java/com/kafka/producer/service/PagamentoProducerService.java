package com.kafka.producer.service;

import com.kafka.producer.model.Pagamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PagamentoProducerService {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoProducerService.class);

    @Value("${app.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, Pagamento> kafkaTemplate;

    public PagamentoProducerService(KafkaTemplate<String, Pagamento> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarMensagem(Pagamento pagamento) {
        try {
            // O KafkaTemplate é tipado, então ele espera um objeto Pagamento.
            // A serialização para Avro é feita automaticamente pelo KafkaAvroSerializer.
            kafkaTemplate.send(topic, pagamento.getId(), pagamento);
            logger.info("Mensagem de pagamento enviada com sucesso para o tópico {}: {}", topic, pagamento);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem de pagamento: {}", pagamento, e);
        }
    }
}