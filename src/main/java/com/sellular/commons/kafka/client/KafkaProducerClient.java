package com.sellular.commons.kafka.client;

import com.sellular.commons.kafka.constants.HeaderConstants;
import com.sellular.commons.kafka.constants.KafkaEventInterface;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class KafkaProducerClient {

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
