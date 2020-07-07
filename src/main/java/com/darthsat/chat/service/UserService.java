package com.darthsat.chat.service;

import com.darthsat.chat.entity.Role;
import com.darthsat.chat.entity.User;
import com.darthsat.chat.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Getter
    private User currentUser;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User getOrCreateUserByName(String userName) {
        try {
            return loadUserByUsername(userName);
        } catch (UsernameNotFoundException e) {
            System.out.println("Creating new user " + userName);
            return userRepository.save(new User(userName));
        }
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

    @Override
    public User loadUserByUsername(String username) {
        return findUser(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional
    public void setCurrentUserPassword(String password) {
        currentUser.setPassword(passwordEncoder.encode(password));
        saveUser(currentUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        Optional<User> userById = userRepository.findById(user.getUsername());
        return userById.isEmpty() ? userRepository.save(user) : null;
    }

    public User deleteUser(String userName) {
        Optional<User> userById = userRepository.findById(userName);
        if (userById.isEmpty()) {
            return null;
        }
        userRepository.delete(userById.get());
        return userById.get();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }
}
