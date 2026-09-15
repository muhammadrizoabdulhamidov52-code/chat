package com.example.twitter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private AppUser user;

    public PostLike() {
    }

    public PostLike(Post post, AppUser user) {
        this.post = post;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public AppUser getUser() {
        return user;
    }
}
