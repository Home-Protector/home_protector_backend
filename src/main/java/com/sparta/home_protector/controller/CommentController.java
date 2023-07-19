package com.sparta.home_protector.controller;

import com.sparta.home_protector.dto.CommentRequestDto;
import com.sparta.home_protector.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{postid}/comment")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성 API
    @PostMapping()
    public ResponseEntity<Map<String,String>> createComment(@RequestHeader(name="Authorization") String tokenValue,
                                                            @PathVariable Long postid,
                                                            @RequestBody @Valid CommentRequestDto requestDto
                                                            ) {
        return commentService.createComment(tokenValue, postid,requestDto);
    }

    // 댓글 수정 API
    @PutMapping("/{commentid}")
    public ResponseEntity<Map<String,String>> updateComment(@RequestHeader(name="Authorization") String tokenValue,
                                                            @PathVariable Long commentid,
                                                            @RequestBody @Valid CommentRequestDto requestDto) {
        return commentService.updateComment(tokenValue, commentid, requestDto);
    }

    // 댓글 삭제 API
    @DeleteMapping("/{commentid}")
    public ResponseEntity<Map<String,String>> deleteComment(@RequestHeader(name="Authorization") String tokenValue,
                                                            @PathVariable Long commentid) {
        return commentService.deleteComment(tokenValue, commentid);
    }
}