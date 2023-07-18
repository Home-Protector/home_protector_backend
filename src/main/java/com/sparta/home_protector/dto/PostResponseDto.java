package com.sparta.home_protector.dto;

import com.sparta.home_protector.entity.Comment;
import com.sparta.home_protector.entity.Post;
import lombok.Getter;

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

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.images = post.getImages();
        this.viewCount = post.getViewCount();
        this.comments = post.getCommentList();
    }
}
