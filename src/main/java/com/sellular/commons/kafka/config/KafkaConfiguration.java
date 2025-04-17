package com.sellular.commons.kafka.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class KafkaConfiguration {
    private List<String> bootstrapServers;
    private String clientId;
    private List<String> schemaRegistryServers;
    private ProducerConfiguration producerConfiguration;
    private List<ConsumerConfiguration> consumers;
}
