package com.example.twitter.repository;
import com.example.twitter.model.AppUser;
import com.example.twitter.model.BookMark;
import com.example.twitter.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
    Optional<BookMark> findByUserAndPost(AppUser user, Post post);
    List<BookMark> findByUserOrderByCreatedAtDesc(AppUser user);
}
