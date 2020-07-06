package com.darthsat.chat.configuration.serializer;

import com.darthsat.chat.messaging.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class MessageDeserializer implements Deserializer<Message> {

    @Override
    public Message deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(data, Message.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
