package com.sparta.home_protector.dto;

import com.sparta.home_protector.entity.Comment;
import com.sparta.home_protector.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
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

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.images = post.getImages();
        this.viewCount = post.getViewCount();
        this.countLikes = post.getPostLikeList().size();
        this.commentList = post.getCommentList().stream()
                .map(CommentResponseDto::new)
                .sorted(Comparator.comparing(CommentResponseDto::getUpdatedAt).reversed())
                .toList();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
