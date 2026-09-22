package com.example.twitter.service;
import com.example.twitter.model.AppUser;
import com.example.twitter.model.Comment;
import com.example.twitter.model.Notification;
import com.example.twitter.model.Post;
import com.example.twitter.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void notify(AppUser recipient, AppUser actor, String type, Post post, Comment comment) {
        if (recipient.getUsername().equals(actor.getUsername())) {
            return;
        }
        notificationRepository.save(new Notification(recipient, actor, type, post, comment));
    }

    public List<Notification> getNotifications(AppUser user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(AppUser user) {
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    public void markAllAsRead(AppUser user) {
        List<Notification> unread = notificationRepository.findByRecipientAndIsReadFalse(user);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
