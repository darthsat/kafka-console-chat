package com.darthsat.chat.service;

import com.darthsat.chat.entity.Role;
import com.darthsat.chat.entity.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    @Getter
    private User currentUser;

    @Autowired
    private CrudRepository<User, String> userRepository;

    public User getOrCreateUserByName(String userName) {
        Optional<User> user = findUser(userName);
        return user.orElseGet(() -> userRepository.save(new User(userName, Role.USER, new ArrayList<>())));
    }

    public Optional<User> findUser(String userName) {
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
