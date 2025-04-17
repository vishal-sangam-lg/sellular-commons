package com.sellular.commons.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KafkaConfigUtil {

    public static Map<String, Object> getProducerProperties(final KafkaConfiguration kafkaConfig) {
        Map<String, Object> props = new HashMap<>();

        // Set bootstrap servers
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaConfig.getBootstrapServers()));

        // Set clientId
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientId());

        // Set serializer classes
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerConfiguration().getKeySerializerClass());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducerConfiguration().getValueSerializerClass());

        // Other producer configs
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        log.info("Kafka Producer Properties Configured");

        return props;
    }
}
