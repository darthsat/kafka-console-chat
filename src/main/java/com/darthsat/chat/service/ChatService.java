package com.darthsat.chat.service;

import com.darthsat.chat.entity.Chat;
import com.darthsat.chat.entity.User;
import com.darthsat.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private UserService userService;

    @Transactional
    public boolean createGroupChat(Chat chat) {
        messagingService.createGroupChatConsumer(userService.getCurrentUser().getUserName(), chat.getChatName());
        chatRepository.save(chat);
        User currentUser = userService.getCurrentUser();
        currentUser.getChats().add(chat);
        userService.saveUser(currentUser);
        return true;
    }

    public Chat findChatByName(String name) {
        Chat chat = chatRepository.findById(name).get();
        if (chat == null) {
            System.out.println("no such chat");
        }
        return chat;
    }

    @Transactional
    public void addUserToChat(Chat chat, User user) {
        if (user.getChats().stream().anyMatch(x -> x.equals(chat))) {
            return;
        }
        user.getChats().add(chat);
        userService.saveUser(user);
    }

    @Transactional
    public void deleteUserFromChat(String chatName, String userName) {
        Chat chat = chatRepository.findById(chatName).get();
        Optional<User> user = userService.findUser(userName);
        if (chat == null || user.isEmpty()) {
            return;
        }

        boolean removed = user.get().getChats().removeIf(x -> x.getChatName().equals(chatName));
        System.out.println(removed ? "User " + userName + " deleted." : "no such user in chat");
        if (removed) {
            messagingService.sendDeleteCommand(chatName, userName);
            userService.saveUser(user.get());
        }
    }

    public void printChatHistory(String chatName) {
        System.out.println("***HISTORY FOR CHAT " + chatName + "***");
        messagingService.getHistoryForChat(chatName).forEach(x -> System.out.println(x.getMessage()));
    }

}
