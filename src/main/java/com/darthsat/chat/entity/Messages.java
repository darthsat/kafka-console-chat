package com.darthsat.chat.entity;

import com.darthsat.chat.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_messages", indexes = @Index(columnList = "chatName", name = "chat_index"))
public class Messages {

    @Id
    @GeneratedValue
    private long messageId;

    @JoinColumn(name = "chat_name", table = "app_chats")
    private String chatName;

    @Embedded
    private Message message;
}
