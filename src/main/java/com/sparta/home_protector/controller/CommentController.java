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
    public ResponseEntity<Map<String,String>> createComment(@PathVariable Long postid,
                                                            @RequestBody @Valid CommentRequestDto requestDto,
                                                            HttpServletRequest request) {
        User user = (User) jwtUtil.getUserInfo(String.valueOf(request));

        return commentService.createComment(postid,requestDto,user);
    }

    // 댓글 수정 API
    @PutMapping("/{commentid}")
    public ResponseEntity<Map<String,String>> updateComment(@PathVariable Long commentid,
                                                            @RequestBody @Valid CommentRequestDto requestDto,
                                                            HttpServletRequest request) {
        User user = (User) jwtUtil.getUserInfo(String.valueOf(request));

        return commentService.updateComment(commentid, requestDto, user);
    }
    // HttpServletRequest
    // 댓글 삭제 API
    @DeleteMapping("/{commentid}")
    public ResponseEntity<Map<String,String>> deleteComment(@PathVariable Long commentid,
                                                            HttpServletRequest request) {
        User user = (User) jwtUtil.getUserInfo(String.valueOf(request));

        return commentService.deleteComment(commentid, user);
    }
}