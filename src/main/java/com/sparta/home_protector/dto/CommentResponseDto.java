package com.sparta.home_protector.dto;

import com.sparta.home_protector.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long comment_id;
    private String comment;
    private String comment_nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Comment comment) {
        this.comment_id = comment.getId();
        this.comment = comment.getComment();
        this.comment_nickname = comment.getUser().getNickname();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}