package com.darthsat.chat;

import com.darthsat.chat.runner.Runner;
import com.darthsat.chat.service.ChatService;
import com.darthsat.chat.service.MessagingService;
import com.darthsat.chat.service.UserService;


public class ChatApplication {

    public static void main(String[] args) {
        UserService userService = new UserService();
        MessagingService messagingService = new MessagingService(userService);
        ChatService chatService = new ChatService(messagingService, userService);

        new Runner(userService, chatService, messagingService).run();
    }
}

