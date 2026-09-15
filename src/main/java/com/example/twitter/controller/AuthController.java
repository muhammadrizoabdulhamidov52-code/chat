package com.example.twitter.controller;

import com.example.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String fullName,
                            Model model) {
        if (userService.usernameExists(username)) {
            model.addAttribute("error", "Bu username band, boshqasini tanlang");
            return "register";
        }
        userService.register(username, password, fullName);
        return "redirect:/login";
    }
}
