package com.darthsat.chat.service;

import com.darthsat.chat.entity.Messages;
import com.darthsat.chat.messaging.DeleteUserCommand;
import com.darthsat.chat.messaging.Message;
import com.darthsat.chat.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class MessagingService {

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, DeleteUserCommand> kafkaTemplateDelete;

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory;

    private List<DeleteUserCommand> deleteUserCommandList = Collections.synchronizedList(new LinkedList<>());

    public boolean isUserDeletedFrom(String chatName) {
        return deleteUserCommandList.removeIf(x -> x.getChatName().equals(chatName) &&
                x.getUsername().equals(userService.getCurrentUser().getUsername()));
    }

    public void createGroupChatConsumer(String userName, String chatName) {
        ConcurrentMessageListenerContainer<String, Message> messagesContainer = kafkaListenerContainerFactory.createContainer(chatName);
        messagesContainer.getContainerProperties().setGroupId(userName);
        messagesContainer.setupMessageListener((MessageListener<String, Object>) x -> {
            if (x.value() instanceof Message) {
                System.out.println(x.value());
            } else if (x.value() instanceof DeleteUserCommand &&
                    ((DeleteUserCommand) x.value()).getUsername().equals(userName) &&
                    ((DeleteUserCommand) x.value()).getChatName().equals(chatName)) {
                deleteUserCommandList.add((DeleteUserCommand) x.value());
                System.out.println("you have been deleted from chat " + chatName);
                messagesContainer.stop();
            }
        });
        messagesContainer.start();
        createHistoryConsumer(chatName);
    }

    public void createPrivateChatConsumer(String userName) {
        ConcurrentMessageListenerContainer<String, Message> container = kafkaListenerContainerFactory.createContainer(userName);
        container.getContainerProperties().setGroupId(userName);
        container.setupMessageListener((MessageListener<String, Message>) x -> System.out.println("[PRIVATE MESSAGE] " + x.value()));
        container.start();
    }

    public void sendMessage(String chat, String message) {
        kafkaTemplate.send(chat, new Message(userService.getCurrentUser().getUsername(), message, System.currentTimeMillis()));
    }

    public void sendDeleteCommand(String chat, String userName) {
        kafkaTemplateDelete.send(chat, new DeleteUserCommand(userName, chat));
    }

    public List<Messages> getHistoryForChat(String chatName) {
        return messagesRepository.findAllByChatNameOrderByMessageTimeAsc(chatName);
    }

    private void createHistoryConsumer(String topic) {
        ConcurrentMessageListenerContainer<String, Message> container = kafkaListenerContainerFactory.createContainer(topic);
        container.getContainerProperties().setGroupId("history-consumer");
        container.setupMessageListener((MessageListener<String, Object>) x -> {
            if (x.value() instanceof Message) {
                Messages messages = new Messages();
                messages.setChatName(topic);
                messages.setMessage((Message) x.value());
                messagesRepository.save(messages);
            }
        });
        container.start();
    }

}
