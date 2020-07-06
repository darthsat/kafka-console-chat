package com.darthsat.chat.repository;

import com.darthsat.chat.entity.Chat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;


public class ChatRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("kafka-chat");
    private EntityManager em = emf.createEntityManager();

    public Chat findChatById(String chatName) {
        List<Chat> resultList = em.createQuery("SELECT c FROM Chat c WHERE c.chatName = :chatName", Chat.class)
                .setParameter("chatName", chatName).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public void save(Chat chat) {
        em.getTransaction().begin();
        em.persist(chat);
        em.getTransaction().commit();
    }
}