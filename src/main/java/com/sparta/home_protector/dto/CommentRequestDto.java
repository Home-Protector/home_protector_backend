package com.sparta.home_protector.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class CommentRequestDto {
    @NotBlank
    private String comment;
}