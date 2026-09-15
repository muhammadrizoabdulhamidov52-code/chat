package com.example.twitter.repository;

import com.example.twitter.model.AppUser;
import com.example.twitter.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByAuthorOrderByCreatedAtDesc(AppUser author);
}
