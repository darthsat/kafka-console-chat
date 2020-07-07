package com.darthsat.chat.controller;

import com.darthsat.chat.entity.Chat;
import com.darthsat.chat.service.ChatService;
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
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("get-all")
    public List<Chat> getAllChats() {
        return chatService.getAllChats();
    }

    @GetMapping("get/{chatName}")
    public Chat getChatById(@PathVariable String chatName) {
        return chatService.findChatByName(chatName);
    }

    @PostMapping("create-chat")
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) {
        return new ResponseEntity<>(chatService.createChat(chat), HttpStatus.OK);
    }

    @PutMapping("update-chat")
    public ResponseEntity<Chat> updateChat(@RequestBody Chat chat) {
        Chat updateChat = chatService.updateChat(chat);
        return updateChat == null ? new ResponseEntity<>(chat, HttpStatus.NOT_MODIFIED) : new ResponseEntity<>(updateChat, HttpStatus.OK);
    }

    @DeleteMapping("delete-chat/{chatName}")
    public ResponseEntity<Chat> deleteChat(@PathVariable String chatName) {
        Chat chat = chatService.deleteChat(chatName);
        return chat == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(chat, HttpStatus.OK);
    }

}