package com.example.twitter.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class DirectMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser sender;

    @ManyToOne
    private AppUser receiver;

    @ManyToOne
    private Post sharedPost;

    private String content;
    private LocalDateTime createdAt;

    public DirectMessage() {
    }

    public DirectMessage(AppUser sender, AppUser receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public DirectMessage(AppUser sender, AppUser receiver, String content, Post sharedPost) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sharedPost = sharedPost;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getSender() {
        return sender;
    }

    public AppUser getReceiver() {
        return receiver;
    }

    public Post getSharedPost() {
        return sharedPost;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
