package com.sparta.home_protector.dto;

import com.sparta.home_protector.entity.Post;
import com.sparta.home_protector.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;
    private String nickname;
    private String title;
    private String content;
    private List<String> images;
    private int viewCount;
    private Integer countLikes;
    private List<CommentResponseDto> commentList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isLike;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.images = new ArrayList<>(post.getImages().values());
        this.viewCount = post.getViewCount();
        this.countLikes = post.getPostLikeList().size();
        this.commentList = post.getCommentList().stream()
                .map(CommentResponseDto::new)
                .sorted(Comparator.comparing(CommentResponseDto::getUpdatedAt).reversed())
                .toList();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    public PostResponseDto(Post post, boolean isLike) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.images = new ArrayList<>(post.getImages().values());
        this.viewCount = post.getViewCount();
        this.countLikes = post.getPostLikeList().size();
        this.commentList = post.getCommentList().stream()
                .map(CommentResponseDto::new)
                .sorted(Comparator.comparing(CommentResponseDto::getUpdatedAt).reversed())
                .toList();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.isLike = isLike;
    }
}
