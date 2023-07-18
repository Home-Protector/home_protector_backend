package com.sparta.home_protector.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostRequestDto {
    private String title;
    private String content;
    private List<MultipartFile> images;

    public PostRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
//        this.images = files;
    }
}
