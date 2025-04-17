package com.sellular.commons.kafka.config;

import lombok.Data;

@Data
public class ConsumerConfiguration {
    private String eventName;
    private boolean active;
    private TopicConfiguration topicConfiguration;
}