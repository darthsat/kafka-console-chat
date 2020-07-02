package com.darthsat.chat.service;

import com.darthsat.chat.entity.Role;
import com.darthsat.chat.entity.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    @Getter
    private User currentUser;

    @Autowired
    private UserService userService;

    @Autowired
    private CrudRepository<User, String> userRepository;

    @Transactional
    public User getOrCreateUserByName(String userName) {
        Optional<User> user = findUser(userName);
        return user.orElseGet(() -> userRepository.save(new User(userName, Role.USER, new ArrayList<>())));
    }

    public Optional<User> findUser(String userName) {
        return userRepository.findById(userName);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void setCurrentUserByName(String userName) {
        currentUser = getOrCreateUserByName(userName);
    }

    @Transactional
    public void makeAdmin(User user) {
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    public void makeCurrentUserAdmin() {
        userService.makeAdmin(currentUser);
        System.out.println("you're an admin now");
    }

    public boolean isCurrentUserAdmin() {
        return currentUser.getRole().equals(Role.ADMIN);
    }
}
