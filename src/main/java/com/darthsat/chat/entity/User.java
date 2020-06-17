package com.darthsat.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_users")
public class User {

    @Id
    private String userName;

    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Chat> chats;
}
