package com.darthsat.chat.service;

import com.darthsat.chat.configuration.KafkaConfiguration;
import com.darthsat.chat.entity.Messages;
import com.darthsat.chat.messaging.DeleteUserCommand;
import com.darthsat.chat.messaging.Message;
import com.darthsat.chat.repository.MessagesRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.darthsat.chat.configuration.KafkaConfiguration.DELETE_USER_COMMAND_DESERIALIZER;
import static com.darthsat.chat.configuration.KafkaConfiguration.DELETE_USER_COMMAND_SERIALIZER;
import static com.darthsat.chat.configuration.KafkaConfiguration.MESSAGE_DESERIALIZER;
import static com.darthsat.chat.configuration.KafkaConfiguration.MESSAGE_SERIALIZER;

public class MessagingService {

    private UserService userService;

    private MessagesRepository messagesRepository = new MessagesRepository();

    private List<DeleteUserCommand> deleteUserCommandList = Collections.synchronizedList(new LinkedList<>());

    private Producer<String, Message> producer;

    private Producer<String, DeleteUserCommand> producerDelete;

    public MessagingService(UserService userService) {
        this.userService = userService;
    }

    public void init(String userName) {
        producer = new KafkaProducer<>(KafkaConfiguration.getKafkaProps(userName,
                MESSAGE_SERIALIZER, MESSAGE_DESERIALIZER));
        producerDelete = new KafkaProducer<>(KafkaConfiguration.getKafkaProps(userName + "_DEL",
                KafkaConfiguration.DELETE_USER_COMMAND_SERIALIZER, KafkaConfiguration.DELETE_USER_COMMAND_DESERIALIZER));

        createPrivateChatConsumer(userName);
    }

    public boolean isUserDeletedFrom(String chatName) {
        return deleteUserCommandList.removeIf(x -> x.getChatName().equals(chatName) &&
                x.getUserName().equals(userService.getCurrentUser().getUserName()));
    }

    public void createGroupChatConsumer(String userName, String chatName) {
        Consumer<String, Message> consumer = new KafkaConsumer<>(KafkaConfiguration.getKafkaProps(userName,
                MESSAGE_SERIALIZER, MESSAGE_DESERIALIZER));
        consumer.subscribe(List.of(chatName));
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, Message> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord x : records) {
                if (x.value() instanceof Message) {
                    System.out.println((x.value()));
                }
            }
        }, 0, 1, TimeUnit.SECONDS);


        createDeleteFromChatConsumer(userName, chatName, consumer, exec);
        createHistoryConsumer(chatName);
    }

    public void createDeleteFromChatConsumer(String userName, String chatName, Consumer<String, Message> chatConsumer,
                                             ScheduledExecutorService chatExec) {
        try (Consumer<String, Message> consumer = new KafkaConsumer<>(KafkaConfiguration.getKafkaProps(userName,
                DELETE_USER_COMMAND_SERIALIZER, DELETE_USER_COMMAND_DESERIALIZER))) {
            consumer.subscribe(List.of(userName + "_DEL"));
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(() -> {
                ConsumerRecords<String, Message> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord x : records) {
                    if (x.value() instanceof DeleteUserCommand &&
                            ((DeleteUserCommand) x.value()).getUserName().equals(userName) &&
                            ((DeleteUserCommand) x.value()).getChatName().equals(chatName)) {
                        deleteUserCommandList.add((DeleteUserCommand) x.value());
                        chatExec.shutdown();
                        chatConsumer.close();
                        System.out.println("you have been deleted from chat " + chatName);
                        consumer.close();
                        exec.shutdown();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    public void createPrivateChatConsumer(String userName) {
        Consumer<String, Message> consumer = new KafkaConsumer<>(KafkaConfiguration.getKafkaProps(userName,
                MESSAGE_SERIALIZER, MESSAGE_DESERIALIZER));
        consumer.subscribe(List.of(userName));
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, Message> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord x : records) {
                if (x.value() instanceof Message) {
                    System.out.println("[PRIVATE MESSAGE] " + x.value());
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void sendMessage(String chat, String message) {
        final ProducerRecord<String, Message> record =
                new ProducerRecord<>(chat,
                        new Message(userService.getCurrentUser().getUserName(), message, System.currentTimeMillis()));
        producer.send(record);
        producer.flush();

    }

    public void sendDeleteCommand(String chat, String userName) {
        final ProducerRecord<String, DeleteUserCommand> record =
                new ProducerRecord<>(userName + "_DEL",
                        new DeleteUserCommand(userName, chat));
        producerDelete.send(record);
        producerDelete.flush();
    }

    public List<Messages> getHistoryForChat(String chatName) {
        return messagesRepository.findAllByChatNameOrderByMessageTimeAsc(chatName);
    }

    private void createHistoryConsumer(String topic) {
        Consumer<String, Message> consumer = new KafkaConsumer<>(KafkaConfiguration.getKafkaProps("history-consumer",
                MESSAGE_SERIALIZER, MESSAGE_DESERIALIZER));

        consumer.subscribe(List.of(topic));
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            ConsumerRecords<String, Message> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord x : records) {
                if (x.value() instanceof Message) {
                    Messages messages = new Messages();
                    messages.setChatName(topic);
                    messages.setMessage((Message) x.value());
                    messagesRepository.save(messages);
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
