package com.sparta.home_protector.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class PostRequestDto {
    private String title;
    private String content;
    private List<MultipartFile> images;

    public PostRequestDto(String title, String content, List<MultipartFile> files) {
        this.title = title;
        this.content = content;
        this.images = files;
    }
}
