package com.sparta.home_protector.service;

import com.sparta.home_protector.dto.CommentRequestDto;
import com.sparta.home_protector.entity.Comment;
import com.sparta.home_protector.entity.Post;
import com.sparta.home_protector.entity.User;
import com.sparta.home_protector.jwt.JwtUtil;
import com.sparta.home_protector.repository.CommentRepository;
import com.sparta.home_protector.repository.PostRepository;
import com.sparta.home_protector.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepositoy;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;

    public ResponseEntity<String> createComment(String tokenValue, Long postId, CommentRequestDto requestDto) {
        // 해당 게시글이 DB에 존재하는지 확인
        Post targetPost = postRepository.findById(postId).orElseThrow(() ->
                new NullPointerException("게시글을 찾을 수 없습니다."));

        User currentUser = checkTokenFindUser(tokenValue);

        // requestDto를 포함한 comment 저장에 필요한 값들 담아서 주기
        Comment comment = new Comment(requestDto, targetPost, currentUser);

        // DB 저장 넘겨주기
        Comment saveComment = commentRepositoy.save(comment);

        // Entity -> ResponseDto
        return ResponseEntity.ok("댓글 등록 완료");
    }

    @Transactional
    public ResponseEntity<String> updateComment(String tokenValue, Long commentId, CommentRequestDto requestDto) {
        // 댓글 저장유무 확인
        Comment comment = findComment(commentId);

        User currentUser = checkTokenFindUser(tokenValue);

        // 권한 확인
        checkAuthority(comment, currentUser);

        // 수정
        comment.update(requestDto);

        // Entity -> ResponseDto
        return ResponseEntity.ok("댓글 수정 완료");
    }

    public ResponseEntity<String> deleteComment(String tokenValue, Long commentId) {
        // 댓글 저장유무 확인
        Comment comment = findComment(commentId);

        User currentUser = checkTokenFindUser(tokenValue);

//         권한 확인
        checkAuthority(comment, currentUser);

        // 삭제
        commentRepositoy.delete(comment);
        return ResponseEntity.ok("댓글 삭제 완료");
    }

    // Comment 조회 메서드
    private Comment findComment(Long id) {
        return commentRepositoy.findById(id).orElseThrow(() ->
                new NullPointerException("존재하지 않는 댓글 입니다.")
        );
    }

    // 토큰 유효성 검증 후 유저 찾아서 반환
    private User checkTokenFindUser(String tokenValue) {
        // 토큰 자르기
        String token = jwtUtil.substringToken(tokenValue);

        // 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token Error");
        }

        // 토큰에서 유저 id 가져와서 user 정보 조회
        Claims info = jwtUtil.getUserInfo(token);

        User user = userRepository.findById(Long.parseLong(info.getSubject())).orElseThrow(() ->
                new NullPointerException("Not Found User")
        );

        return user;
    }

    // 추후 관리자 권한 서비스 추가시 이용
    // 수정, 삭제시 권한을 확인
    public void checkAuthority(Comment comment, User user) {
        // 사용자 확인
        if (comment.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
        }
    }
}