package com.darthsat.chat.configuration.serializer;

import com.darthsat.chat.messaging.DeleteUserCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class DeleteUserCommandDeserializer implements Deserializer<DeleteUserCommand> {

    @Override
    public DeleteUserCommand deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, DeleteUserCommand.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
