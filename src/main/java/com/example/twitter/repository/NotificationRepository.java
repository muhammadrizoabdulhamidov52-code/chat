package com.example.twitter.repository;
import com.example.twitter.model.AppUser;
import com.example.twitter.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(AppUser recipient);
    long countByRecipientAndIsReadFalse(AppUser recipient);
    List<Notification> findByRecipientAndIsReadFalse(AppUser recipient);
}



