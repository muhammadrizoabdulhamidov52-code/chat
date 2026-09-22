package com.example.twitter.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class CallSignalingController {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public CallSignalingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/call.signal")
    public void handleSignal(Map<String, Object> payload, Principal principal) {
        String to = (String) payload.get("to");
        if (to == null) {
            return;
        }
        payload.put("from", principal.getName());
        messagingTemplate.convertAndSendToUser(to, "/queue/call", payload);
    }
}
