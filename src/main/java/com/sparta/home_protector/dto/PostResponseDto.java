package com.sparta.home_protector.dto;

import com.sparta.home_protector.entity.Post;
import lombok.Getter;

@Getter
public class PostResponseDto {
    private Long id;
    private String nickname;
    private String title;
    private String content;
    private String img;
    private int viewCount;

    public PostResponseDto(Post post){
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.img = post.getImage();
        this.viewCount = post.getViewCount();
    }
}
