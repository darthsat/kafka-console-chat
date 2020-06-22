package com.darthsat.chat.repository;

import com.darthsat.chat.entity.Messages;
import com.darthsat.chat.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
class MessagesRepositoryTest {

    private MessagesRepository messagesRepository;

    @Autowired
    private EntityManager em;

    @Test
    void findAllByChatNameOrderByMessageTimeAscTest() {
        messagesRepository = new MessagesRepository(em);
        Messages message1 = new Messages();
        message1.setMessage(new Message("asd", "1", System.currentTimeMillis()));
        message1.setChatName("chat");
        em.persist(message1);

        Messages message2 = new Messages();
        message2.setMessage(new Message("asd", "2", System.currentTimeMillis()));
        message2.setChatName("chat");

        Messages message3 = new Messages();
        message3.setMessage(new Message("asd", "3", System.currentTimeMillis()));
        message3.setChatName("chat1");

        Messages message4 = new Messages();
        message4.setMessage(new Message("asd", "4", System.currentTimeMillis()));
        message4.setChatName("chat");

        assertEquals(message1, messagesRepository.findAllByChatNameOrderByMessageTimeAsc("chat").get(0));
        assertEquals(message2, messagesRepository.findAllByChatNameOrderByMessageTimeAsc("chat").get(1));
        assertEquals(message4, messagesRepository.findAllByChatNameOrderByMessageTimeAsc("chat").get(2));

    }

    @Test
    void saveTest() {
        messagesRepository = new MessagesRepository(em);
        Messages message1 = new Messages();
        message1.setMessage(new Message("asd", "1", System.currentTimeMillis()));
        message1.setChatName("chat");
        em.persist(message1);
        messagesRepository.save(message1);

        assertEquals(message1, em.find(Messages.class, 1L));
    }
}