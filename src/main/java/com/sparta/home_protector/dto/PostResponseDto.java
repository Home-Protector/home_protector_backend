package com.sparta.home_protector.dto;

import com.sparta.home_protector.entity.Comment;
import com.sparta.home_protector.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;
    private String nickname;
    private String title;
    private String content;
    private List<String> images;
    private int viewCount;
    private List<Comment> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updateadAt;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.images = new ArrayList<>(post.getImages().values());
        this.viewCount = post.getViewCount();
        this.comments = post.getCommentList();
        this.createdAt = post.getCreatedAt();
        this.updateadAt = post.getUpdatedAt();
    }
}
