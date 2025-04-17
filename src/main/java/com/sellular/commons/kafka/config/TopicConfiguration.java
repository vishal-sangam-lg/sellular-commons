package com.sellular.commons.kafka.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TopicConfiguration {
    private Map<String, String> configOverrides;
    private String keyDeserializerClass;
    private String valueDeserializerClass;
    private List<String> topics;
    private String groupId;
}
