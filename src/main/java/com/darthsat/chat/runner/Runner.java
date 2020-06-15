package com.darthsat.chat.runner;

import com.darthsat.chat.messaging.Message;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Scanner;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Component
public class Runner implements CommandLineRunner {

    private static final String EXIT = "exit";
    private static final String ALL = "all";
    private final Scanner scanner = new Scanner(System.in);

    @Getter
    private String account;

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory;

    @Override
    public void run(String... args) {
        while (account == null) {
            System.out.println("Enter your name:");
            String account = scanner.nextLine();
            if (!isBlank(account)) {
                this.account = account;
            } else {
                System.out.println("Name must not be empty.");
            }
        }

        createGroupChatConsumer();
        createPrivateChatConsumer();
        kafkaTemplate.send(ALL, new Message(account, account + " joined chat", System.currentTimeMillis()));
        System.out.println("Welcome. Type 'pc' for public chat. 'dm' for direct messaging");
        String command = "";
        while (!EXIT.equals(command)) {
            command = scanner.nextLine();
            switch (command) {
                case "pc":
                    command = runPublic();
                    break;
                case "dm":
                    command = runPrivate();
                    break;
                case EXIT:
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    private void createGroupChatConsumer() {
        ConcurrentMessageListenerContainer<String, Message> container = kafkaListenerContainerFactory.createContainer("all");
        container.getContainerProperties().setGroupId(account);
        container.setupMessageListener((MessageListener<String, Message>) x ->
                System.out.println(Instant.ofEpochMilli(x.value().getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime() + " " + x.value().getAccount() + ": " + x.value().getValue()));
        container.start();
    }

    private void createPrivateChatConsumer() {
        ConcurrentMessageListenerContainer<String, Message> container = kafkaListenerContainerFactory.createContainer(account);
        container.getContainerProperties().setGroupId(account);
        container.setupMessageListener((MessageListener<String, Message>) x ->
                System.out.println("[PRIVATE MESSAGE] "
                        + Instant.ofEpochMilli(x.value().getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        + " " + x.value().getAccount() + ": " + x.value().getValue()));
        container.start();
    }

    private String runPublic() {
        System.out.println("Type your message. --dm for direct messaging. --exit to exit app.");
        String command = "";
        while (!EXIT.equals(command)) {
            command = scanner.nextLine();
            switch (command) {
                case "--dm":
                    return runPrivate();
                case "--exit":
                    return EXIT;
                default:
                    kafkaTemplate.send(ALL, new Message(account, command, System.currentTimeMillis()));
                    break;
            }
        }
        return command;
    }

    private String runPrivate() {
        System.out.println("Enter recipient");
        String recipient = scanner.nextLine();
        System.out.println("Type your message. --pc for public chat. --cr to change recipient. --exit to exit app.");
        String command = "";
        while (!EXIT.equals(command)) {
            command = scanner.nextLine();
            switch (command) {
                case "--pc":
                    return runPublic();
                case "--cr":
                    return runPrivate();
                case "--exit":
                    return EXIT;
                default:
                    kafkaTemplate.send(recipient, new Message(account, command, System.currentTimeMillis()));
                    break;
            }
        }
        return command;
    }
}