package com.darthsat.chat.controller;


import com.darthsat.chat.entity.Messages;
import com.darthsat.chat.service.MessagingService;
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
@RequestMapping("/messaging")
public class MessagingController {

    @Autowired
    private MessagingService messagingService;

    @GetMapping("get-all/{chatName}")
    public List<Messages> getAllMessagessByChatName(@PathVariable String chatName) {
        return messagingService.getAllMessagesByChatName(chatName);
    }

    @PostMapping("create-message")
    public ResponseEntity<Messages> createMessage(@RequestBody Messages message) {
        return new ResponseEntity<>(messagingService.createMessage(message), HttpStatus.OK);
    }

    @PutMapping("update-message")
    public ResponseEntity<Messages> updateMessages(@RequestBody Messages message) {
        Messages updateMessages = messagingService.updateMessage(message);
        return updateMessages == null ? new ResponseEntity<>(message, HttpStatus.NOT_MODIFIED) : new ResponseEntity<>(updateMessages, HttpStatus.OK);
    }

    @DeleteMapping("delete-message/{messageId}")
    public ResponseEntity<Messages> deleteMessages(@PathVariable Long messageId) {
        Messages messages = messagingService.deleteMessage(messageId);
        return messages == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(messages, HttpStatus.OK);
    }
}