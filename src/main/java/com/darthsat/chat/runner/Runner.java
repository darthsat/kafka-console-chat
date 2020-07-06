package com.darthsat.chat.runner;

import com.darthsat.chat.entity.Chat;
import com.darthsat.chat.service.ChatService;
import com.darthsat.chat.service.MessagingService;
import com.darthsat.chat.service.UserService;

import java.util.Objects;
import java.util.Scanner;

public class Runner {

    private static final String EXIT = "exit";
    private final Scanner scanner = new Scanner(System.in);

    private UserService userService;
    private ChatService chatService;
    private MessagingService messagingService;

    public Runner(UserService userService, ChatService chatService, MessagingService messagingService) {
        this.userService = userService;
        this.chatService = chatService;
        this.messagingService = messagingService;
    }

    public void run(String... args) {
        while (userService.getCurrentUser() == null) {
            System.out.println("Enter your name:");
            String userName = scanner.nextLine();
            if (!Objects.equals(userName, "")) {
                userService.setCurrentUserByName(userName);
            } else {
                System.out.println("Name must not be empty.");
            }
        }

        messagingService.init(userService.getCurrentUser().getUserName());

        System.out.println("Welcome.");
        String command = "";
        while (!EXIT.equals(command)) {
            System.out.println("Type 'cc' for create chat, 'jc' for joining chat, 'dm' for direct messaging. " +
                    "IDDQD for god mode");
            command = scanner.nextLine();
            switch (command) {
                case "cc":
                    command = runNewChat();
                    break;
                case "jc":
                    command = runJoinChat();
                    break;
                case "dm":
                    command = runPrivate();
                    break;
                case EXIT:
                    return;
                case "IDDQD":
                    userService.makeCurrentUserAdmin();
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    private String runNewChat() {
        String chatName = "";
        while (chatName.isBlank()) {
            System.out.println("Enter chat name:");
            chatName = scanner.nextLine();
            if (!Objects.equals(chatName, "")) {
                Chat chat = new Chat();
                chat.setChatName(chatName);
                if (!chatService.createGroupChat(chat)) {
                    return "";
                }
            } else {
                System.out.println("Name must not be empty.");
            }
        }
        return runPublicChat(chatName);
    }

    private String runJoinChat() {
        String chatName = "";
        Chat chat = null;
        while (chatName.isBlank() || chat == null) {
            System.out.println("Enter chat name:");
            chatName = scanner.nextLine();
            if (!Objects.equals(chatName, "")) {
                chat = chatService.findChatByName(chatName);
                if (chat != null) {
                    chatService.addUserToChat(chat, userService.getCurrentUser());
                    messagingService.createGroupChatConsumer(userService.getCurrentUser().getUserName(), chatName);
                }
            } else {
                System.out.println("Name must not be empty.");
            }
        }
        return runPublicChat(chatName);
    }

    private String runPublicChat(String chatName) {
        String adminHint = userService.isCurrentUserAdmin() ? " --del for deleting users from this chat" : "";
        System.out.println("Type your message. --dm for direct messaging. --exit to exit app. --history for chat history" + adminHint);
        String command = "";
        while (!EXIT.equals(command)) {
            command = scanner.nextLine();
            if (messagingService.isUserDeletedFrom(chatName)) {
                return "";
            }
            switch (command) {
                case "--dm":
                    return runPrivate();
                case "--exit":
                    return EXIT;
                case "--history":
                    chatService.printChatHistory(chatName);
                    break;
                case "--del":
                    runDeleteUsers(chatName);
                    System.out.println("Type your message. --dm for direct messaging. --exit to exit app. --history for chat history" + adminHint);
                    break;
                default:
                    messagingService.sendMessage(chatName, command);
                    break;
            }
        }
        return command;
    }

    private void runDeleteUsers(String chatName) {
        if (!userService.isCurrentUserAdmin()) {
            System.out.println("nope");
            return;
        }
        String command = "";
        while (true) {
            System.out.println("which user?");
            command = scanner.nextLine();
            if (!command.isBlank()) {
                chatService.deleteUserFromChat(chatName, command);
            }
            System.out.println("are you done? (y/n)");
            command = scanner.nextLine();
            switch (command) {
                case "y":
                    return;
                case "n":
                    break;
                default:
                    System.out.println("type 'y' or 'n'");
            }
        }
    }

    private String runPrivate() {
        System.out.println("Enter recipient");
        String recipient = scanner.nextLine();
        System.out.println("Type your message. --rt for return. --cr to change recipient. --exit to exit app.");
        String command = "";
        while (!EXIT.equals(command)) {
            command = scanner.nextLine();
            switch (command) {
                case "rt":
                    return "";
                case "--cr":
                    return runPrivate();
                case "--exit":
                    return EXIT;
                default:
                    messagingService.sendMessage(recipient, command);
                    break;
            }
        }
        return command;
    }
}