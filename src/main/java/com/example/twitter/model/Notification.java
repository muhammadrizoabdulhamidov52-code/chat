package com.example.twitter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser recipient;

    @ManyToOne
    private AppUser actor;

    private String type;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Comment comment;

    private LocalDateTime createdAt;
    private boolean isRead = false;

    public Notification() {
    }

    public Notification(AppUser recipient, AppUser actor, String type, Post post, Comment comment) {
        this.recipient = recipient;
        this.actor = actor;
        this.type = type;
        this.post = post;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Long getId() {
        return id;
    }

    public AppUser getRecipient() {
        return recipient;
    }

    public AppUser getActor() {
        return actor;
    }

    public String getType() {
        return type;
    }

    public Post getPost() {
        return post;
    }

    public Comment getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
