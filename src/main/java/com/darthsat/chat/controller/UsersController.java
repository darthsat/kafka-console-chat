package com.darthsat.chat.controller;

import com.darthsat.chat.entity.User;
import com.darthsat.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping("get-all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("get/{userName}")
    public User getUserById(@PathVariable String userName) {
        return userService.findUser(userName).orElse(null);
    }

    @PostMapping("create-user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }

    @PutMapping("update-user")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updateUser = userService.updateUser(user);
        return updateUser == null ? new ResponseEntity<>(user, HttpStatus.NOT_MODIFIED) : new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    @DeleteMapping("delete-user/{userName}")
    public ResponseEntity<User> deleteUser(@PathVariable String userName) {
        User user = userService.deleteUser(userName);
        return user == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(user, HttpStatus.OK);
    }
}