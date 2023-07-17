package com.sparta.home_protector.entity;


import com.sparta.home_protector.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // JPA가 관리할 Entitu 클래스 저장
@Getter
@Table(name = "comment") //  매핑할 테이블명
@NoArgsConstructor
public class Comment extends Timestamped{

    @Id // 식별자
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment 걸어주기
    private Long id;

    @Column(name = "comment", nullable = false, length = 100)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    private Post post;

    public void update(CommentRequestDto requestDto) {
        this.comment = requestDto.getComment();
    }
}
