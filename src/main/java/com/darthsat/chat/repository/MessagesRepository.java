package com.darthsat.chat.repository;

import com.darthsat.chat.entity.Messages;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class MessagesRepository {

    private EntityManager em;

    MessagesRepository(EntityManager entityManager) {
        em = entityManager;
    }

    public List<Messages> findAllByChatNameOrderByMessageTimeAsc(String chatName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Messages> cq = cb.createQuery(Messages.class);
        Root<Messages> messages = cq.from(Messages.class);
        TypedQuery<Messages> query = em.createQuery(cq.where(cb.equal(messages.get("chatName"), chatName))
                .orderBy(cb.asc(messages.get("message").get("time"))));
        return query.getResultList();
    }

    public void save(Messages messages) {
        em.persist(messages);
    }
}