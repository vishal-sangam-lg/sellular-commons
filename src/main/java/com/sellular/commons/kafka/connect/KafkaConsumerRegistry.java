package com.sellular.commons.kafka.connect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KafkaConsumerRegistry {

    private static final Set<KafkaConsumerClient<?, ?>> KAFKA_CONSUMER_REGISTRIES = new HashSet<>();

    public static void registerConsumer(final KafkaConsumerClient<?, ?> kafkaConsumerClient) {
        KAFKA_CONSUMER_REGISTRIES.add(kafkaConsumerClient);
    }

    public static void registerConsumers(final KafkaConsumerClient<?, ?>... kafkaConsumerClients) {
        KAFKA_CONSUMER_REGISTRIES.addAll(Arrays.asList(kafkaConsumerClients));
    }

    public static void startConsuming() {
        KAFKA_CONSUMER_REGISTRIES.forEach(KafkaConsumerClient::startConsuming);
    }

    public static void shutdown() {
        KAFKA_CONSUMER_REGISTRIES.forEach(KafkaConsumerClient::shutdown);
    }

}

