package com.example.twitter.service;

import com.example.twitter.model.AppUser;
import com.example.twitter.model.Post;
import com.example.twitter.model.PostLike;
import com.example.twitter.repository.PostLikeRepository;
import com.example.twitter.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Autowired
    public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public Post createPost(AppUser author, String content) {
        Post post = new Post(author, content);
        return postRepository.save(post);
    }

    public List<Post> getFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getPostsByUser(AppUser user) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(user);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow();
    }

    public Post viewPost(Long id) {
        Post post = getPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }

    public long getLikeCount(Post post) {
        return postLikeRepository.countByPost(post);
    }

    public boolean isLikedByUser(Post post, AppUser user) {
        return postLikeRepository.findByPostAndUser(post, user).isPresent();
    }

    public void toggleLike(Long postId, AppUser user) {
        Post post = getPostById(postId);
        Optional<PostLike> existing = postLikeRepository.findByPostAndUser(post, user);
        if (existing.isEmpty()) {
            postLikeRepository.save(new PostLike(post, user));
        } else {
            postLikeRepository.delete(existing.get());

        }
    }
}
