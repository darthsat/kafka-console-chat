package com.darthsat.chat.service;

import com.darthsat.chat.entity.Chat;
import com.darthsat.chat.entity.User;
import com.darthsat.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
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
        messagingService.createGroupChatConsumer(userService.getCurrentUser().getUsername(), chat.getChatName());
        chatRepository.save(chat);
        User currentUser = userService.getCurrentUser();
        currentUser.getChats().add(chat);
        userService.saveUser(currentUser);
        return true;
    }

    public Chat findChatByName(String name) {
        Optional<Chat> chat = chatRepository.findById(name);
        if (chat.isEmpty()) {
            System.out.println("no such chat");
        }
        return chat.orElse(null);
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
        Optional<Chat> chat = chatRepository.findById(chatName);
        Optional<User> user = userService.findUser(userName);
        if (chat.isEmpty() || user.isEmpty()) {
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

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Chat updateChat(Chat chat) {
        Optional<Chat> chatById = chatRepository.findById(chat.getChatName());
        return chatById.isEmpty() ? chatRepository.save(chat) : null;
    }

    public Chat deleteChat(String chatName) {
        Optional<Chat> chatById = chatRepository.findById(chatName);
        if (chatById.isEmpty()) {
            return null;
        }
        chatRepository.delete(chatById.get());
        return chatById.get();
    }

    public Chat createChat(Chat chat) {
        return chatRepository.save(chat);
    }
}
