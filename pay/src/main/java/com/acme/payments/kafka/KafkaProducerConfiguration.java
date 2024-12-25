package com.acme.payments.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.acme.payments.model.Payment;

@EnableTransactionManagement
@Configuration
public class KafkaProducerConfiguration {
	
	@Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    ProducerFactory<String, Payment> paymentProducerFactory() {
        Map<String, Object> configProps = new HashMap<String, Object>();

        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        // EOS - Exactly once semantics, set to override defaults for kafka < 3.0.0
        configProps.put
                (ProducerConfig.TRANSACTIONAL_ID_CONFIG,
                        "transaction-id");
        configProps.put(
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                true);
        configProps.put(
                ProducerConfig.ACKS_CONFIG,
                "all");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, Payment> paymentKafkaTemplate() {
        return new KafkaTemplate<>(paymentProducerFactory());
    }
	
}
