package com.sparta.home_protector.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class PostRequestDto {
    private String title;
    private String content;
    private MultipartFile image;

    public PostRequestDto(String title, String content, MultipartFile multipartFile) {
        this.title = title;
        this.content = content;
        this.image = multipartFile;
    }
}
