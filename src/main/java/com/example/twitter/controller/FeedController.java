package com.example.twitter.controller;

import com.example.twitter.model.AppUser;
import com.example.twitter.model.Post;
import com.example.twitter.service.PostService;
import com.example.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FeedController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public FeedController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String feed(Model model, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        List<Post> posts = postService.getFeed();

        Map<Long, Long> likeCounts = new HashMap<>();
        Map<Long, Boolean> likedByMe = new HashMap<>();
        for (Post post : posts) {
            likeCounts.put(post.getId(), postService.getLikeCount(post));
            likedByMe.put(post.getId(), postService.isLikedByUser(post, currentUser));
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", posts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("likedByMe", likedByMe);
        return "feed";
    }

    @PostMapping("/posts/create")
    public String createPost(@RequestParam String content, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        postService.createPost(currentUser, content);
        return "redirect:/";
    }

    @PostMapping("/posts/{id}/like")
    public String like(@PathVariable Long id, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        postService.toggleLike(id, currentUser);
        return "redirect:/";
    }

    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Long id, Model model, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        Post post = postService.viewPost(id);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("post", post);
        model.addAttribute("likeCount", postService.getLikeCount(post));
        model.addAttribute("likedByMe", postService.isLikedByUser(post, currentUser));
        return "post-detail";
    }
}
