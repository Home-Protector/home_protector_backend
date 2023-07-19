package com.sparta.home_protector.controller;

import com.sparta.home_protector.dto.PostRequestDto;
import com.sparta.home_protector.dto.PostResponseDto;
import com.sparta.home_protector.jwt.JwtUtil;
import com.sparta.home_protector.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
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
        // JWT 검증 및 요청한 UserId 반환
        Long userId = validateTokenAndGetUserId(httpServletRequest);

        // Request Dto 객체 생성
        PostRequestDto postRequestDto = new PostRequestDto(title, content, files);

        return postService.createPost(postRequestDto, userId);
    }

    // 게시글 조회 API (?sort=createdAt(최신순) || ?sort=viewCount(조회순) || sort = countLikes(좋아요순))
    @GetMapping("/post")
    public List<PostResponseDto> getAllPost(@RequestParam(name = "sort", required = false) String sort) {
        if (sort == null || sort.isEmpty()){
            sort = "default";
        }
        return postService.getAllPost(sort);
    }

    // 게시글 상세 조회 API (조회수 로직 포함)
    @GetMapping("/post/{postId}")
    public PostResponseDto getPostDetail(@PathVariable Long postId,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        return postService.getPostDetail(postId, request, response);
    }

    // 게시글 수정 API
    @PutMapping("/post/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable Long postId,
                                             @RequestPart("title") String title,
                                             @RequestPart("content") String content,
                                             @RequestPart(value = "images", required = false) List<MultipartFile> files,
                                             HttpServletRequest httpServletRequest) throws AccessDeniedException {
        // JWT 검증 및 요청한 UserId 반환
        Long userId = validateTokenAndGetUserId(httpServletRequest);

        // Request Dto 생성
        PostRequestDto postRequestDto = new PostRequestDto(title, content, files);

        return postService.updatePost(postRequestDto, postId, userId);
    }

    // 게시글 삭제 API
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, HttpServletRequest httpServletRequest) throws AccessDeniedException {
        // JWT 검증 및 요청한 UserId 반환
        Long userId = validateTokenAndGetUserId(httpServletRequest);

        return postService.deletePost(postId, userId);
    }

    // 게시글 좋아요 API
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<String> likeBoard(@PathVariable Long postId,
                                            HttpServletRequest httpServletRequest) {

        // JWT 검증 및 요청한 UserId 반환
        Long userId = validateTokenAndGetUserId(httpServletRequest);

        return postService.likePost(postId, userId);
    }

    //     JWT 검증 및 사용자 정보(UserId) 반환 메서드
    private Long validateTokenAndGetUserId(HttpServletRequest httpServletRequest) {
        // JWT 토큰 조회 및 가공
        String token = jwtUtil.substringToken(jwtUtil.getTokenFromRequest(httpServletRequest));

        // JWT 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 검증에 실패했습니다.");
        }

        // 요청한 사용자 정보(Id)
        Long userId = Long.parseLong(jwtUtil.getUserInfo(token).getSubject());

        return userId;
    }
}
