package com.darthsat.chat.entity;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "app_chats")
public class Chat extends ChatAbstractEntity {

    public Chat(String chatName) {
        super(chatName);
    }
}
