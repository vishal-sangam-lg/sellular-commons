package com.sellular.commons.kafka.client;

import com.sellular.commons.kafka.config.KafkaConfiguration;
import com.sellular.commons.kafka.constants.HeaderConstants;
import com.sellular.commons.kafka.constants.KafkaEventInterface;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import java.util.Properties;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class KafkaProducerClient {

    public KafkaProducerClient(final KafkaConfiguration config) {
        this.producer = new KafkaProducer<>(buildProducerProps(config));
        log.info("✅ KafkaProducer initialized for bootstrap servers: {}", config.getBootstrapServers());
    }

    private Properties buildProducerProps(KafkaConfiguration config) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.getProducerConfiguration().getKeySerializerClass());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config.getProducerConfiguration().getValueSerializerClass());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        props.put("schema.registry.url", config.getSchemaRegistryServers());
        return props;
    }


    private final Producer<String, byte[]> producer;

    public void publish(byte[] messageBytes, KafkaEventInterface event, String key) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(event.getTopic(), key, messageBytes);
        Optional.ofNullable(MDC.get(HeaderConstants.X_TRANSACTION_ID))
                .ifPresentOrElse(xTransactionId -> record.headers().add(HeaderConstants.X_TRANSACTION_ID, xTransactionId.getBytes()), () -> {
                    MDC.put(HeaderConstants.X_TRANSACTION_ID, UUID.randomUUID().toString());
                    record.headers().add(HeaderConstants.X_TRANSACTION_ID, MDC.get(HeaderConstants.X_TRANSACTION_ID).getBytes());
                });
        try {
            producer.send(record, (metadata, exception) -> {
                if (Objects.nonNull(exception)) {
                    log.error("Error publishing event {} to topic {}", event.getEventType(), event.getTopic(), exception);
                } else {
                    log.info("Published event {} to topic {} with key {}", event.getEventType(), event.getTopic(), key);
                    log.info("Publish Event Metadata: \n Offset: {} \n Topic: {} \n Partition :{} \n", metadata.offset(), metadata.topic(), metadata.partition());
                }
            });
        } catch (final Exception e) {
            log.error("Error while producing event");
        }
    }

}
