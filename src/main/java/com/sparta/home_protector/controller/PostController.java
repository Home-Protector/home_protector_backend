package com.sparta.home_protector.controller;

import com.sparta.home_protector.dto.PostResponseDto;
import com.sparta.home_protector.dto.PostRequestDto;
import com.sparta.home_protector.jwt.JwtUtil;
import com.sparta.home_protector.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j(topic = "Post 컨트롤러")
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
    public ResponseEntity<String> createPost(@RequestPart("title") String title,
                                             @RequestPart("content") String content,
                                             @RequestPart("images") List<MultipartFile> files,
                                             HttpServletRequest httpServletRequest) {

        PostRequestDto postRequestDto = new PostRequestDto(title, content, files);

        // JWT 조회 및 검증
        String token = jwtUtil.substringToken(httpServletRequest.getHeader("Authorization"));

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 검증에 실패했습니다.");
        }

        // 요청한 사용자 정보(Id)
        Long tokenId = Long.parseLong(jwtUtil.getUserInfo(token).getSubject());

        return postService.createPost(postRequestDto, tokenId);
    }

    // 게시글 조회 API
    @GetMapping("/post")
    public List<PostResponseDto> getAllPost() {
        return postService.getAllPost();
    }

    // 게시글 상세 조회 API
    @GetMapping("/post/{postId}")
    public PostResponseDto getPostDetail(@PathVariable Long postId){
        return postService.getPostDetail(postId);
    }

    // 게시글 수정 API
    @PutMapping("/post/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable Long postId,
                                             @RequestPart("title") String title,
                                             @RequestPart("content") String content,
                                             @RequestPart(value = "images", required = false) List<MultipartFile> files,
                                             HttpServletRequest httpServletRequest) {
        // JWT 토큰 조회 및 가공
        String token = jwtUtil.substringToken(httpServletRequest.getHeader("Authorization"));

        // JWT 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 검증에 실패했습니다.");
        }

        // 요청한 사용자 정보(Id)
        Long tokenId = Long.parseLong(jwtUtil.getUserInfo(token).getSubject());

        // Request Dto 생성
        PostRequestDto postRequestDto = new PostRequestDto(title, content, files);

        return postService.updatePost(postRequestDto, postId, tokenId);
    }

    // 게시글 삭제 API
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, HttpServletRequest httpServletRequest){
        // JWT 토큰 조회 및 가공
        String token = jwtUtil.substringToken(httpServletRequest.getHeader("Authorization"));

        // JWT 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 검증에 실패했습니다.");
        }

        // 요청한 사용자 정보(Id)
        Long tokenId = Long.parseLong(jwtUtil.getUserInfo(token).getSubject());

        return postService.deletePost(postId, tokenId);
    }
}
