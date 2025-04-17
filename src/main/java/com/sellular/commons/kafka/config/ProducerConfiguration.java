package com.sellular.commons.kafka.config;

import lombok.Data;

import java.util.Map;

@Data
public class ProducerConfiguration {
    private boolean autoRegisterSchemas;
    private String keySerializerClass;
    private String valueSerializerClass;
    private Map<String, String> eventTopicsMap; // eventName -> topicName
}