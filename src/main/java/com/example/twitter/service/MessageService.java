package com.example.twitter.service;

import com.example.twitter.model.AppUser;
import com.example.twitter.model.DirectMessage;
import com.example.twitter.model.Post;
import com.example.twitter.repository.DirectMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {

    private final DirectMessageRepository directMessageRepository;

    @Autowired
    public MessageService(DirectMessageRepository directMessageRepository) {
        this.directMessageRepository = directMessageRepository;
    }

    public DirectMessage sendMessage(AppUser sender, AppUser receiver, String content) {
        DirectMessage message = new DirectMessage(sender, receiver, content);
        return directMessageRepository.save(message);
    }
    public DirectMessage sharePost(AppUser sender, AppUser receiver, Post post, String note) {
        String content = (note != null && !note.isEmpty()) ? note : "Post ulashdi";
        DirectMessage message = new DirectMessage(sender, receiver, content, post);
        return directMessageRepository.save(message);
    }

    public List<DirectMessage> getConversation(AppUser userA, AppUser userB) {
        return directMessageRepository.findConversation(userA, userB);
    }

    public List<AppUser> getConversationPartners(AppUser user) {
        Set<AppUser> partners = new LinkedHashSet<>();
        partners.addAll(directMessageRepository.findReceiversFromUser(user));
        partners.addAll(directMessageRepository.findSendersToUser(user));
        return partners.stream().toList();
    }
}