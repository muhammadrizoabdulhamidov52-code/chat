package com.example.twitter.service;
import com.example.twitter.model.AppUser;
import com.example.twitter.model.BookMark;
import com.example.twitter.model.Post;
import com.example.twitter.repository.BookMarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookMarkService {
    private final BookMarkRepository bookmarkRepository;

    @Autowired
    public BookMarkService(BookMarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    public void toggleBookmark(AppUser user, Post post) {
        Optional<BookMark> existing = bookmarkRepository.findByUserAndPost(user, post);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
        } else {
            bookmarkRepository.save(new BookMark(user, post));
        }
    }

    public boolean isBookmarked(AppUser user, Post post) {
        return bookmarkRepository.findByUserAndPost(user, post).isPresent();
    }

    public List<BookMark> getBookmarks(AppUser user) {
        return bookmarkRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
