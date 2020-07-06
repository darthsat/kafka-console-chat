package com.darthsat.chat.service;

import com.darthsat.chat.entity.Chat;
import com.darthsat.chat.entity.User;
import com.darthsat.chat.repository.ChatRepository;


public class ChatService {

    private ChatRepository chatRepository = new ChatRepository();
    private MessagingService messagingService;
    private UserService userService;

    public ChatService(MessagingService messagingService, UserService userService) {
        this.messagingService = messagingService;
        this.userService = userService;
    }

    public boolean createGroupChat(Chat chat) {
        messagingService.createGroupChatConsumer(userService.getCurrentUser().getUserName(), chat.getChatName());
        chatRepository.save(chat);
        User currentUser = userService.getCurrentUser();
        currentUser.getChats().add(chat);
        userService.saveUser(currentUser);
        return true;
    }

    public Chat findChatByName(String name) {
        Chat chat = chatRepository.findChatById(name);
        if (chat == null) {
            System.out.println("no such chat");
        }
        return chat;
    }

    public void addUserToChat(Chat chat, User user) {
        if (user.getChats().stream().anyMatch(x -> x.equals(chat))) {
            return;
        }
        user.getChats().add(chat);
        userService.saveUser(user);
    }

    public void deleteUserFromChat(String chatName, String userName) {
        Chat chat = chatRepository.findChatById(chatName);
        User user = userService.findUser(userName);
        if (chat == null || user == null) {
            return;
        }

        boolean removed = user.getChats().removeIf(x -> x.getChatName().equals(chatName));
        System.out.println(removed ? "User " + userName + " deleted." : "no such user in chat");
        if (removed) {
            messagingService.sendDeleteCommand(chatName, userName);
            userService.saveUser(user);
        }
    }

    public void printChatHistory(String chatName) {
        System.out.println("***HISTORY FOR CHAT " + chatName + "***");
        messagingService.getHistoryForChat(chatName).forEach(x -> System.out.println(x.getMessage()));
    }

}
