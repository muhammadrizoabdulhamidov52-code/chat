package com.example.twitter.controller;
import com.example.twitter.model.AppUser;
import com.example.twitter.service.MessageService;
import com.example.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("/messages")
    public String conversationsList(Model model, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("partners", messageService.getConversationPartners(currentUser));
        return "messages-list";
    }

    @GetMapping("/messages/{username}")
    public String conversation(@PathVariable String username, Model model, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        AppUser otherUser = userService.getByUsername(username);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("messages", messageService.getConversation(currentUser, otherUser));
        return "message-thread";
    }

    @PostMapping("/messages/{username}/send")
    public String send(@PathVariable String username, @RequestParam String content, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        AppUser otherUser = userService.getByUsername(username);
        messageService.sendMessage(currentUser, otherUser, content);
        return "redirect:/messages/" + username;
    }
}
