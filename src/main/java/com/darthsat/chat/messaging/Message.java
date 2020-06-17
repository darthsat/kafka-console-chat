package com.darthsat.chat.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.Instant;
import java.time.ZoneId;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String userName;
    private String value;
    private Long time;

    @Override
    public String toString() {
        return Instant.ofEpochMilli(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime() + " " + userName + ": " + value;
    }
}