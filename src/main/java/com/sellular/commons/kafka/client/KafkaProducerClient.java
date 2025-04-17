package com.sellular.commons.kafka.client;

import com.sellular.commons.kafka.config.KafkaConfiguration;
import com.sellular.commons.kafka.config.KafkaConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Map;

@Slf4j
public class KafkaProducerClient {

    private KafkaProducer<String, byte[]> producer;

    public KafkaProducerClient(final KafkaConfiguration kafkaConfiguration) {
        Map<String, Object> producerProps = KafkaConfigUtil.getProducerProperties(kafkaConfiguration);
        this.producer = new KafkaProducer<>(producerProps);
    }

    public void sendMessage(final String topic, final String key, final byte[] message) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, message);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                log.info("Message sent to topic {} with key {}", topic, key);
            }
        });
    }

    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}

