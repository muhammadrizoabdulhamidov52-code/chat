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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Controller
public class ProfileController {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public ProfileController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model, Authentication authentication) {
        AppUser profileUser = userService.getByUsername(username);
        AppUser currentUser = userService.getByUsername(authentication.getName());
        List<Post> posts = postService.getPostsByUser(profileUser);

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isOwnProfile", profileUser.getUsername().equals(currentUser.getUsername()));
        model.addAttribute("posts", posts);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                 @RequestParam String bio,
                                 @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                                 Authentication authentication) throws IOException {

        String photoUrl = null;
        if (photoFile != null && !photoFile.isEmpty()) {
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String extension = "";
            String originalName = photoFile.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + extension;
            Path filePath = Path.of(uploadDir.getAbsolutePath(), fileName);
            Files.write(filePath, photoFile.getBytes());
            photoUrl = "/uploads/" + fileName;
        }

        userService.updateProfile(authentication.getName(), fullName, bio, photoUrl);
        return "redirect:/profile/" + authentication.getName();
    }
}
