package com.example.twitter.repository;

import com.example.twitter.model.AppUser;
import com.example.twitter.model.Post;
import com.example.twitter.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, AppUser user);
    long countByPost(Post post);
}
