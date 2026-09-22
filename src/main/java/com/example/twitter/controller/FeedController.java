package com.example.twitter.controller;

import com.example.twitter.model.AppUser;
import com.example.twitter.model.Post;
import com.example.twitter.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class FeedController {

    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final BookMarkService bookmarkService;
    private final MessageService messageService;


    @Autowired
    public FeedController(PostService postService, UserService userService, NotificationService notificationService,
                          BookMarkService bookmarkService, MessageService messageService) {
        this.postService = postService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.bookmarkService = bookmarkService;
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String feed(Model model, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        List<Post> posts = postService.getFeed();

        Map<Long, Long> likeCounts = new HashMap<>();
        Map<Long, Boolean> likedByMe = new HashMap<>();
        Map<Long, Boolean> bookmarkedByMe = new HashMap<>();
        for (Post post : posts) {
            likeCounts.put(post.getId(), postService.getLikeCount(post));
            likedByMe.put(post.getId(), postService.isLikedByUser(post, currentUser));
            bookmarkedByMe.put(post.getId(), bookmarkService.isBookmarked(currentUser, post));
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", posts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("bookmarkedByMe", bookmarkedByMe);
        model.addAttribute("totalPosts", postService.getTotalPostsCount());
        model.addAttribute("totalUsers", userService.getTotalUsersCount());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(currentUser));
        model.addAttribute("conversationPartners", messageService.getConversationPartners(currentUser));
        return "feed";
    }

    @PostMapping("/posts/create")
    public String createPost(@RequestParam String content,
                             @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile,
                             @RequestParam(value = "videoFile", required = false) org.springframework.web.multipart.MultipartFile videoFile,
                             Authentication authentication) throws java.io.IOException {
        AppUser currentUser = userService.getByUsername(authentication.getName());

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = saveUploadedFile(imageFile);
        }

        String videoUrl = null;
        if (videoFile != null && !videoFile.isEmpty()) {
            videoUrl = saveUploadedFile(videoFile);
        }

        postService.createPost(currentUser, content, imageUrl, videoUrl);
        return "redirect:/";
    }

    private String saveUploadedFile(org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        java.io.File uploadDir = new java.io.File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = java.util.UUID.randomUUID() + extension;
        java.nio.file.Path filePath = java.nio.file.Path.of(uploadDir.getAbsolutePath(), fileName);
        java.nio.file.Files.write(filePath, file.getBytes());
        return "/uploads/" + fileName;
    }

    @PostMapping("/posts/{id}/like")
    public String like(@org.springframework.web.bind.annotation.PathVariable Long id, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        postService.toggleLike(id, currentUser);
        return "redirect:/";
    }

    @PostMapping("/posts/{id}/bookmark")
    public String bookmark(@org.springframework.web.bind.annotation.PathVariable Long id,
                           @RequestParam(required = false) String redirectTo,
                           Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        Post post = postService.getPostById(id);
        bookmarkService.toggleBookmark(currentUser, post);
        if (redirectTo != null && redirectTo.equals("bookmarks")) {
            return "redirect:/bookmarks";
        }
        return "redirect:/";
    }

    @GetMapping("/bookmarks")
    public String bookmarksPage(Model model, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        List<com.example.twitter.model.BookMark> bookmarks = bookmarkService.getBookmarks(currentUser);

        Map<Long, Long> likeCounts = new HashMap<>();
        Map<Long, Boolean> likedByMe = new HashMap<>();
        Map<Long, Boolean> bookmarkedByMe = new HashMap<>();
        for (com.example.twitter.model.BookMark b : bookmarks) {
            Post post = b.getPost();
            likeCounts.put(post.getId(), postService.getLikeCount(post));
            likedByMe.put(post.getId(), postService.isLikedByUser(post, currentUser));
            bookmarkedByMe.put(post.getId(), true);
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("bookmarks", bookmarks);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("bookmarkedByMe", bookmarkedByMe);
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(currentUser));
        return "bookmarks";
    }

    @PostMapping("/posts/{id}/share")
    public String sharePost(@org.springframework.web.bind.annotation.PathVariable Long id,
                            @RequestParam String toUsername,
                            @RequestParam(required = false) String note,
                            Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        AppUser receiver = userService.getByUsername(toUsername);
        Post post = postService.getPostById(id);
        messageService.sharePost(currentUser, receiver, post, note);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/posts/{id}")
    public String postDetail(@org.springframework.web.bind.annotation.PathVariable Long id, Model model, Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        Post post = postService.viewPost(id);
        List<com.example.twitter.model.Comment> topComments = postService.getTopLevelComments(post);

        Map<Long, java.util.List<com.example.twitter.model.Comment>> repliesMap = new HashMap<>();
        Map<Long, Long> commentLikeCounts = new HashMap<>();
        Map<Long, Boolean> commentLikedByMe = new HashMap<>();

        for (com.example.twitter.model.Comment comment : topComments) {
            repliesMap.put(comment.getId(), postService.getReplies(comment));
            commentLikeCounts.put(comment.getId(), postService.getCommentLikeCount(comment));
            commentLikedByMe.put(comment.getId(), postService.isCommentLikedByUser(comment, currentUser));

            for (com.example.twitter.model.Comment reply : postService.getReplies(comment)) {
                commentLikeCounts.put(reply.getId(), postService.getCommentLikeCount(reply));
                commentLikedByMe.put(reply.getId(), postService.isCommentLikedByUser(reply, currentUser));
            }
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("post", post);
        model.addAttribute("likeCount", postService.getLikeCount(post));
        model.addAttribute("likedByMe", postService.isLikedByUser(post, currentUser));
        model.addAttribute("bookmarked", bookmarkService.isBookmarked(currentUser, post));
        model.addAttribute("comments", topComments);
        model.addAttribute("repliesMap", repliesMap);
        model.addAttribute("commentLikeCounts", commentLikeCounts);
        model.addAttribute("commentLikedByMe", commentLikedByMe);
        model.addAttribute("totalCommentCount", postService.getCommentCount(post));
        model.addAttribute("conversationPartners", messageService.getConversationPartners(currentUser));
        return "post-detail";
    }

    @PostMapping("/posts/{id}/comment")
    public String addComment(@org.springframework.web.bind.annotation.PathVariable Long id,
                             @RequestParam String content,
                             Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        postService.addComment(id, currentUser, content);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/comments/{commentId}/reply")
    public String addReply(@org.springframework.web.bind.annotation.PathVariable Long commentId,
                           @RequestParam String content,
                           @RequestParam Long postId,
                           Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        postService.addReply(commentId, currentUser, content);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{commentId}/like")
    public String likeComment(@org.springframework.web.bind.annotation.PathVariable Long commentId,
                              @RequestParam Long postId,
                              Authentication authentication) {
        AppUser currentUser = userService.getByUsername(authentication.getName());
        postService.toggleCommentLike(commentId, currentUser);
        return "redirect:/posts/" + postId;
    }
}
