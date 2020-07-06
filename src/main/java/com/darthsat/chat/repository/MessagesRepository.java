package com.darthsat.chat.repository;

import com.darthsat.chat.entity.Messages;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


public class MessagesRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("kafka-chat");
    private EntityManager em = emf.createEntityManager();

    public List<Messages> findAllByChatNameOrderByMessageTimeAsc(String chatName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Messages> cq = cb.createQuery(Messages.class);
        Root<Messages> messages = cq.from(Messages.class);
        TypedQuery<Messages> query = em.createQuery(cq.where(cb.equal(messages.get("chatName"), chatName))
                .orderBy(cb.asc(messages.get("message").get("time"))));
        return query.getResultList();
    }

    public void save(Messages messages) {
        em.getTransaction().begin();
        em.persist(messages);
        em.getTransaction().commit();
    }
}