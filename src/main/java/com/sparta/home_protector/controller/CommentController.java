package com.sparta.home_protector.controller;

import com.sparta.home_protector.dto.CommentRequestDto;
import com.sparta.home_protector.entity.User;
import com.sparta.home_protector.jwt.JwtUtil;
import com.sparta.home_protector.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{postid}/comment")
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    // 댓글 작성 API
    @PostMapping()
    public ResponseEntity<Map<String,String>> createComment(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                                            @PathVariable Long postid,
                                                            @RequestBody @Valid CommentRequestDto requestDto,
                                                            HttpServletRequest request) {
        User user = (User) jwtUtil.getUserInfo(String.valueOf(request));

        return commentService.createComment(tokenValue, postid,requestDto,user);
    }

    // 댓글 수정 API
    @PutMapping("/{commentid}")
    public ResponseEntity<Map<String,String>> updateComment(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                                            @PathVariable Long commentid,
                                                            @RequestBody @Valid CommentRequestDto requestDto,
                                                            HttpServletRequest request) {
        User user = (User) jwtUtil.getUserInfo(String.valueOf(request));

        return commentService.updateComment(tokenValue, commentid, requestDto, user);
    }

    // 댓글 삭제 API
    @DeleteMapping("/{commentid}")
    public ResponseEntity<Map<String,String>> deleteComment(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                                            @PathVariable Long commentid,
                                                            HttpServletRequest request) {
        User user = (User) jwtUtil.getUserInfo(String.valueOf(request));

        return commentService.deleteComment(tokenValue, commentid, user);
    }
}