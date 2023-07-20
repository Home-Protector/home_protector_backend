package com.sparta.home_protector.controller;

import com.sparta.home_protector.dto.CommentRequestDto;
import com.sparta.home_protector.dto.CommentResponseDto;
import com.sparta.home_protector.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{postid}/comment")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성 API
    @PostMapping()
    public ResponseEntity<String> createComment(@RequestHeader(name = "Authorization") String tokenValue,
                                                @PathVariable Long postid,
                                                @RequestBody @Valid CommentRequestDto requestDto
    ) {
        return commentService.createComment(tokenValue, postid, requestDto);
    }

    // 게시글에 있는 댓글 목록을 반환하는 API
    @GetMapping()
    public List<CommentResponseDto> getCommentList(@PathVariable Long postid) {
        return commentService.getCommentList(postid);
    }

    // 댓글 수정 API
    @PutMapping("/{commentid}")
    public ResponseEntity<String> updateComment(@RequestHeader(name = "Authorization") String tokenValue,
                                                @PathVariable Long commentid,
                                                @RequestBody @Valid CommentRequestDto requestDto) {
        return commentService.updateComment(tokenValue, commentid, requestDto);
    }

    // 댓글 삭제 API
    @DeleteMapping("/{commentid}")
    public ResponseEntity<String> deleteComment(@RequestHeader(name = "Authorization") String tokenValue,
                                                @PathVariable Long commentid) {
        return commentService.deleteComment(tokenValue, commentid);
    }
}