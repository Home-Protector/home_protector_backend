package com.sparta.home_protector.controller;

import com.sparta.home_protector.dto.PostRequestDto;
import com.sparta.home_protector.jwt.JwtUtil;
import com.sparta.home_protector.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PostController {
    private final PostService postService;
    private final JwtUtil jwtUtil;
    public PostController(PostService postService, JwtUtil jwtUtil) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
    }

    // 게시글 작성 API
    @PostMapping("/post")
    public ResponseEntity<String> createPost(@ModelAttribute PostRequestDto postRequestDto, HttpServletRequest httpServletRequest){
        // JWT 조회 및 검증
        String token = jwtUtil.substringToken(httpServletRequest.getHeader("Authorization"));
        if (!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("토큰 검증에 실패했습니다.");
        }
        // 요청한 사용자 확인(Id)
        Long tokenId = Long.parseLong(jwtUtil.getUserInfo(token).getId());

        return postService.createPost(postRequestDto, tokenId);
    }
}
