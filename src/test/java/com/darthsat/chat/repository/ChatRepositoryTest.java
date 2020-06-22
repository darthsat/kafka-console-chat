package com.darthsat.chat.repository;

import com.darthsat.chat.entity.Chat;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;
    private Chat expected;

    @Before
    public void init() {
        expected = new Chat("bar");
        List<Chat> chatList = List.of(new Chat("foo"), expected, new Chat("baz"));
        chatRepository.saveAll(chatList);
    }

    @Test
    void findChatByIdTest() {
        assertEquals(expected, chatRepository.findChatById("bar"));
    }
}