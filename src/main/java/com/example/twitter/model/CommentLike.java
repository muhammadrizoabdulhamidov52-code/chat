package com.example.twitter.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Comment comment;

    @ManyToOne
    private AppUser user;

    public CommentLike() {
    }

    public CommentLike(Comment comment, AppUser user) {
        this.comment = comment;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Comment getComment() {
        return comment;
    }

    public AppUser getUser() {
        return user;
    }
}
