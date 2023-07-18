package com.sparta.home_protector.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image", nullable = false)
    private String image;  // 이미지 파일을 서버 또는 외부 스토리지(S3 Bucket)에 업로드하고, 경로를 db에 저장

    @Column(name = "viewCount", nullable = false)
    private int viewCount;

    @ManyToOne //@JoinColumn 해야할까?
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;
}
