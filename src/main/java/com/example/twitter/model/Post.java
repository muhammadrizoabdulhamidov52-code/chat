package com.example.twitter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser author;

    private String content;
    private LocalDateTime createdAt;
    private Integer viewCount = 0;

    public Post() {
    }

    public Post(AppUser author, String content) {
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
    }

    public Long getId() {
        return id;
    }

    public AppUser getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}
