package com.darthsat.chat.entity;

import com.darthsat.chat.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MessagePK.class)
@Table(name = "app_messages", indexes = @Index(columnList = "chatName", name = "chat_index"))
public class Messages extends ChatAbstractEntity implements Serializable {

    @Id
    @GeneratedValue
    private long messageId;

    @Embedded
    private Message message;
}
