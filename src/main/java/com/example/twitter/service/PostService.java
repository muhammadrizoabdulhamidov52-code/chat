package com.example.twitter.service;

import com.example.twitter.model.AppUser;

import com.example.twitter.model.Comment;
import com.example.twitter.model.CommentLike;
import com.example.twitter.model.Post;
import com.example.twitter.model.PostLike;
import com.example.twitter.repository.CommentLikeRepository;
import com.example.twitter.repository.CommentRepository;
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
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final NotificationService notificationService;

    @Autowired
    public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository,
                       CommentRepository commentRepository, CommentLikeRepository commentLikeRepository,
                       NotificationService notificationService) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.notificationService = notificationService;
    }

    public Post createPost(AppUser author, String content, String imageUrl, String videoUrl) {
        Post post = new Post(author, content, imageUrl, videoUrl);
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
        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get());
        } else {
            postLikeRepository.save(new PostLike(post, user));
            notificationService.notify(post.getAuthor(), user, "LIKE", post, null);
        }
    }

    public Comment addComment(Long postId, AppUser author, String content) {
        Post post = getPostById(postId);
        Comment comment = new Comment(post, author, content);
        Comment saved = commentRepository.save(comment);
        notificationService.notify(post.getAuthor(), author, "COMMENT", post, saved);
        return saved;
    }

    public Comment addReply(Long parentCommentId, AppUser author, String content) {
        Comment parent = commentRepository.findById(parentCommentId).orElseThrow();
        Comment reply = new Comment(parent.getPost(), author, content, parent);
        Comment saved = commentRepository.save(reply);
        notificationService.notify(parent.getAuthor(), author, "REPLY", parent.getPost(), saved);
        return saved;
    }

    public List<Comment> getTopLevelComments(Post post) {
        return commentRepository.findByPostAndParentIsNullOrderByCreatedAtAsc(post);
    }

    public List<Comment> getReplies(Comment comment) {
        return commentRepository.findByParentOrderByCreatedAtAsc(comment);
    }

    public long getCommentLikeCount(Comment comment) {
        return commentLikeRepository.countByComment(comment);
    }

    public boolean isCommentLikedByUser(Comment comment, AppUser user) {
        return commentLikeRepository.findByCommentAndUser(comment, user).isPresent();
    }

    public void toggleCommentLike(Long commentId, AppUser user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Optional<CommentLike> existing = commentLikeRepository.findByCommentAndUser(comment, user);
        if (existing.isPresent()) {
            commentLikeRepository.delete(existing.get());
        } else {
            commentLikeRepository.save(new CommentLike(comment, user));
            notificationService.notify(comment.getAuthor(), user, "COMMENT_LIKE", comment.getPost(), comment);
        }
    }

    public List<Comment> getComments(Post post) {
        return commentRepository.findByPostOrderByCreatedAtAsc(post);
    }

    public long getCommentCount(Post post) {
        return commentRepository.countByPost(post);
    }

    public long getTotalPostsCount() {
        return postRepository.count();
    }




}
