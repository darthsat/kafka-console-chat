package com.darthsat.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_users")
public class User {

    @Id
    @Column(length = 64)
    private String userName;

    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Chat> chats;
}
