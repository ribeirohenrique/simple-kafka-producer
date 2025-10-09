package com.kafka.producer;

import com.kafka.producer.model.Pagamento;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${KAFKA_BOOTSTRAP_SERVERS}")
    private String bootstrapServers;

    @Value("${SCHEMA_REGISTRY_URL}")
    private String schemaRegistryUrl;

    @Value("${KAFKA_CLIENT_ID}")
    private String clientId;

    @Value("${KAFKA_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${OKTA_SCOPE}")
    private String scope;

    @Value("${CONFLUENT_LOGICAL_CLUSTER_ID}")
    private String logicalClusterId;

    @Value("${CONFLUENT_IDENTITY_POOL_ID}")
    private String identityPoolId;

    @Value("${OKTA_TOKEN_ENDPOINT_URL}")
    private String tokenEndpointUrl;

    @Bean
    public ProducerFactory<String, Pagamento> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // ===============================
        // Kafka Bootstrap + Segurança OAuth
        // ===============================
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put("security.protocol", "SASL_SSL");
        configProps.put("sasl.mechanism", "OAUTHBEARER");
        configProps.put("sasl.login.callback.handler.class", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginCallbackHandler");
        configProps.put("sasl.oauthbearer.token.endpoint.url", tokenEndpointUrl);

        // JAAS config com Logical Cluster
        configProps.put("sasl.jaas.config",
                "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required " +
                        "clientId=\"" + clientId + "\" " +
                        "clientSecret=\"" + clientSecret + "\" " +
                        "scope=\"" + scope + "\" " +
                        "extension_logicalCluster=\"" + logicalClusterId + ";");

        // ===============================
        // Serializadores
        // ===============================
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        // ===============================
        // Schema Registry
        // ===============================
        configProps.put("schema.registry.url", schemaRegistryUrl);
        configProps.put("bearer.auth.credentials.source", "SASL_OAUTHBEARER_INHERIT");
//        configProps.put("bearer.auth.issuer.endpoint.url", tokenEndpointUrl);
//        configProps.put("bearer.auth.client.id", clientId);
//        configProps.put("bearer.auth.client.secret", clientSecret);
        configProps.put("bearer.auth.logical.cluster", logicalClusterId);
        configProps.put("bearer.auth.identity.pool.id", identityPoolId);

        // ===============================
        // Producer adicional
        // ===============================
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 5);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Pagamento> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
