package com.darthsat.chat.configuration;

import com.darthsat.chat.configuration.serializer.DeleteUserCommandDeserializer;
import com.darthsat.chat.configuration.serializer.MessageDeserializer;
import com.darthsat.chat.configuration.serializer.SerializerImpl;
import com.darthsat.chat.messaging.DeleteUserCommand;
import com.darthsat.chat.messaging.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public final class KafkaConfiguration {

    private KafkaConfiguration() {
    }

    public static final SerializerImpl<DeleteUserCommand> DELETE_USER_COMMAND_SERIALIZER = new SerializerImpl<>();
    public static final DeleteUserCommandDeserializer DELETE_USER_COMMAND_DESERIALIZER = new DeleteUserCommandDeserializer();
    public static final SerializerImpl<Message> MESSAGE_SERIALIZER = new SerializerImpl<>();
    public static final MessageDeserializer MESSAGE_DESERIALIZER = new MessageDeserializer();

    public static Properties getKafkaProps(String username, Serializer serializer, Deserializer deserializer) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer" + username);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer_" + username);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer.getClass().getName());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass().getName());
        return props;

    }
}
