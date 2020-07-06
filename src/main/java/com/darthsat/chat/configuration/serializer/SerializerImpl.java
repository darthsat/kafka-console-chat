package com.darthsat.chat.configuration.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class SerializerImpl<T> implements Serializer<T> {

    @SneakyThrows
    @Override
    public byte[] serialize(String topic, T obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);

    }
}
