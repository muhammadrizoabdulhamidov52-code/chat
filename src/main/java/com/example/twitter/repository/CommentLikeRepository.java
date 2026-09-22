package com.example.twitter.repository;
import com.example.twitter.model.AppUser;
import com.example.twitter.model.Comment;
import com.example.twitter.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, AppUser user);
    long countByComment(Comment comment);

}
