package com.darthsat.chat.repository;

import com.darthsat.chat.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;


public class UserRepository {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("kafka-chat");
    private EntityManager em = emf.createEntityManager();

    public User findById(String userName) {
        List<User> resultList = em.createQuery("SELECT u FROM User u WHERE u.userName = :userName", User.class)
                .setParameter("userName", userName).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public User save(User user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return user;
    }
}
