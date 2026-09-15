package com.example.twitter.controller;
import com.example.twitter.model.AppUser;
import com.example.twitter.model.Post;
import com.example.twitter.service.PostService;
import com.example.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostRestController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getFeed();
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postService.viewPost(id);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, String> body, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        String content = body.get("content");
        Post post = postService.createPost(currentUser, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long id, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        postService.toggleLike(id, currentUser);
        Post post = postService.getPostById(id);
        boolean liked = postService.isLikedByUser(post, currentUser);
        long count = postService.getLikeCount(post);
        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
    }
}
