package com.sellular.commons.kafka.client;

import com.sellular.commons.kafka.config.KafkaConfiguration;
import com.sellular.commons.kafka.constants.HeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import java.util.*;

@Slf4j
public class KafkaProducerClient<K, V> {

    private final KafkaConfiguration kafkaConfiguration;
    private final Producer<K, V> kafkaProducer;

    public KafkaProducerClient(final KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
        Map<String, Object> config = createBaseKafkaConfigurations();
        this.kafkaProducer = new KafkaProducer<>(config);
    }

    private Map<String, Object> createBaseKafkaConfigurations() {
        Map<String, Object> config = new HashMap<>();
        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaConfiguration.getBootstrapServers()));
        config.put(CommonClientConfigs.CLIENT_ID_CONFIG, kafkaConfiguration.getClientId());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfiguration.getProducerConfiguration().getKeySerializerClass());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfiguration.getProducerConfiguration().getValueSerializerClass());
        config.put("auto.register.schemas", kafkaConfiguration.getProducerConfiguration().isAutoRegisterSchemas());
        Optional.ofNullable(kafkaConfiguration.getSchemaRegistryServers())
                .ifPresent(servers -> config.put("schema.registry.url", String.join(",", servers)));
        return config;
    }

    public void publish(final List<V> events, final String eventName) {
        events.forEach(event -> publish(event, eventName));
    }

    public void publish(final V value, final String eventName) {
        processEvent(value, null, eventName);
    }

    public void publish(final V value, final K key, final String eventName) {
        processEvent(value, key, eventName);
    }

    protected void processEvent(final V value, final K key, final String eventName) {
        log.debug("Publishing record {}", value);

        final Optional<String> topic = Optional.ofNullable(
                kafkaConfiguration.getProducerConfiguration().getEventTopicsMap().get(eventName));

        if (topic.isPresent()) {
            final ProducerRecord<K, V> record = new ProducerRecord<>(topic.get(), key, value);
            Optional.ofNullable(MDC.get(HeaderConstants.X_TRANSACTION_ID))
                    .ifPresentOrElse(
                            xTxnId -> record.headers().add(HeaderConstants.X_TRANSACTION_ID, xTxnId.getBytes()),
                            () -> {
                                MDC.put(HeaderConstants.X_TRANSACTION_ID, UUID.randomUUID().toString());
                                record.headers().add(HeaderConstants.X_TRANSACTION_ID, MDC.get(HeaderConstants.X_TRANSACTION_ID).getBytes());
                            }
                    );
            try {
                kafkaProducer.send(record, (metadata, e) -> {
                    if (Objects.isNull(e)) {
                        log.info("\nEvent metadata: \n Offset: {} \n Topic: {} \n Partition :{} \n Timestamp : {}",
                                metadata.offset(), metadata.topic(), metadata.partition(), metadata.timestamp());
                    } else {
                        log.error("Error sending record: {}", ExceptionUtils.getRootCauseMessage(e), e);
                    }
                });
            } catch (final Exception e) {
                log.error("Error while sending the record: ", e);
            }
        } else {
            log.warn("Topic not found for event: {}", eventName);
        }
    }

    public void shutdown() {
        kafkaProducer.close();
        log.info("Kafka producer closed successfully");
    }
}
