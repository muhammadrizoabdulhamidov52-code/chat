package com.example.twitter.service;

import com.example.twitter.model.AppUser;
import com.example.twitter.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return appUserRepository.existsByUsername(username);
    }

    public AppUser register(String username, String rawPassword, String fullName) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName);
        return appUserRepository.save(user);
    }

    public AppUser getByUsername(String username) {
        return appUserRepository.findByUsername(username).orElseThrow();
    }

    public AppUser updateProfile(String username, String fullName, String bio, String photoUrl) {
        AppUser user = getByUsername(username);
        user.setFullName(fullName);
        user.setBio(bio);
        if (photoUrl != null && !photoUrl.isEmpty()) {
            user.setPhotoUrl(photoUrl);
        }
        return appUserRepository.save(user);
    }

    public long getTotalUsersCount() {
        return appUserRepository.count();
    }
}
