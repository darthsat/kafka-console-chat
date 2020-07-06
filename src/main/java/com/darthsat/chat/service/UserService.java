package com.darthsat.chat.service;

import com.darthsat.chat.entity.Role;
import com.darthsat.chat.entity.User;
import com.darthsat.chat.repository.UserRepository;
import lombok.Getter;

import java.util.ArrayList;


public class UserService {

    @Getter
    private User currentUser;

    private UserRepository userRepository = new UserRepository();

    public User getOrCreateUserByName(String userName) {
        User user = findUser(userName);
        return user != null ? user : userRepository.save(new User(userName, Role.USER, new ArrayList<>()));
    }

    public User findUser(String userName) {
        return userRepository.findById(userName);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void setCurrentUserByName(String userName) {
        currentUser = getOrCreateUserByName(userName);
    }

    public void makeAdmin(User user) {
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    public void makeCurrentUserAdmin() {
        makeAdmin(currentUser);
        System.out.println("you're an admin now");
    }

    public boolean isCurrentUserAdmin() {
        return currentUser.getRole().equals(Role.ADMIN);
    }
}
