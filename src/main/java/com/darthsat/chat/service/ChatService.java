package com.darthsat.chat.service;

import com.darthsat.chat.entity.Chat;
import com.darthsat.chat.entity.User;
import com.darthsat.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private UserService userService;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private EntityManager em;

    @PostConstruct
    public void init() {
        em = entityManagerFactory.createEntityManager();
    }

    public boolean createGroupChat(Chat chat) {
        EntityTransaction tx = em.getTransaction();
        boolean saved = false;
        tx.begin();
        try {
            messagingService.createGroupChatConsumer(userService.getCurrentUser().getUserName(), chat.getChatName());
            chatRepository.save(chat);
            User currentUser = userService.getCurrentUser();
            currentUser.getChats().add(chat);
            userService.saveUser(currentUser);
            tx.commit();
            saved = true;
        } catch (Exception e) {
            System.out.println("Cannot create chat");
            tx.rollback();
        }
        return saved;
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
        Optional<User> user = userService.findUser(userName);
        if (chat == null || user.isEmpty()) {
            return;
        }

        boolean removed = user.get().getChats().removeIf(x -> x.getChatName().equals(chatName));
        System.out.println(removed ? "User " + userName + " deleted." : "no such user in chat");
        if (removed) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            try {
                messagingService.sendDeleteCommand(chatName, userName);
                userService.saveUser(user.get());
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
            }

        }
    }

    public void printChatHistory(String chatName) {
        System.out.println("***HISTORY FOR CHAT " + chatName + "***");
        messagingService.getHistoryForChat(chatName).forEach(x -> System.out.println(x.getMessage()));
    }

}
