package com.darthsat.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/chat")
    public String chat(Model model) {
        return "chat";
    }

    @GetMapping("/admin/page")
    public String admin(Model model) {
        return "admin";
    }
}