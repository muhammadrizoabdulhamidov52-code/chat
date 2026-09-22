package com.example.twitter.controller;

import com.example.twitter.model.AppUser;
import com.example.twitter.service.NotificationService;
import com.example.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String notifications(Model model, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("notifications", notificationService.getNotifications(currentUser));
        notificationService.markAllAsRead(currentUser);
        return "notifications";
    }
}
