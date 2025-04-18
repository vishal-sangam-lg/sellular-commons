package com.sellular.commons.kafka.connect;

import com.sellular.commons.kafka.config.ConsumerConfiguration;
import com.sellular.commons.kafka.config.KafkaConfiguration;
import com.sellular.commons.kafka.config.TopicConfiguration;
import com.sellular.commons.kafka.constants.HeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.slf4j.MDC;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class KafkaConsumerClient<K, V> implements Runnable {

    protected final KafkaConfiguration kafkaConfiguration;
    protected final ConsumerConfiguration consumerConfig;
    protected final TopicConfiguration topicConfig;
    protected final Consumer<K, V> consumer;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean processing = new AtomicBoolean(false);

    public KafkaConsumerClient(KafkaConfiguration kafkaConfig, String eventName) {
        this.kafkaConfiguration = kafkaConfig;
        this.consumerConfig = kafkaConfig.getConsumers().stream()
                .filter(c -> c.getEventName().equalsIgnoreCase(eventName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Consumer config not found for event: " + eventName));

        this.topicConfig = consumerConfig.getTopicConfiguration();
        this.consumer = new KafkaConsumer<>(buildKafkaConfig());
        this.consumer.subscribe(topicConfig.getTopics());
    }

    private Map<String, Object> buildKafkaConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaConfiguration.getBootstrapServers()));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, topicConfig.getKeyDeserializerClass());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, topicConfig.getValueDeserializerClass());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, topicConfig.getGroupId());

        if (topicConfig.getConfigOverrides() != null) {
            config.putAll(topicConfig.getConfigOverrides());
        }

        return config;
    }

    public void startConsuming() {
        log.info("Starting consumer for event: {}", consumerConfig.getEventName());
        running.set(true);
        final Thread consumerThread = new Thread(this, "KafkaConsumer-" + consumerConfig.getEventName());
        consumerThread.start();
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                processing.set(true);

                if (consumerConfig.isActive()) {
                    ConsumerRecords<K, V> records = consumer.poll(Duration.of(
                            topicConfig.getPollFrequency(),
                            topicConfig.getPollFrequencyUnit()
                    ));

                    log.debug("Fetched {} records for event: {}", records.count(), consumerConfig.getEventName());
                    records.forEach(this::processRecord);
                    consumer.commitSync();
                }

            } catch (Exception e) {
                log.error("Error while consuming records for event: {}", consumerConfig.getEventName(), e);
            } finally {
                processing.set(false);
            }
        }
    }

    private void processRecord(ConsumerRecord<K, V> record) {
        setHeaders(record);
        try {
            process(record.key(), record.value());
        } catch (Exception e) {
            log.error("Error processing record from topic: {}", record.topic(), e);
        } finally {
            removeHeaders();
        }
    }

    private static <K, V> void setHeaders(ConsumerRecord<K, V> record) {
        final String transactionId = Optional.ofNullable(record.headers().lastHeader(HeaderConstants.X_TRANSACTION_ID))
                .map(h -> new String(h.value()))
                .orElse(UUID.randomUUID().toString());
        MDC.put(HeaderConstants.X_TRANSACTION_ID, transactionId);
    }

    private void removeHeaders() {
        MDC.remove(HeaderConstants.X_TRANSACTION_ID);
    }

    public void shutdown() {
        log.info("Shutting down consumer for event: {}", consumerConfig.getEventName());
        running.set(false);
        while (processing.get()) {
            Thread.yield(); // Wait for current processing to complete
        }
        consumer.close();
        log.info("Consumer closed for event: {}", consumerConfig.getEventName());
    }

    public abstract void process(K key, V value);
}
