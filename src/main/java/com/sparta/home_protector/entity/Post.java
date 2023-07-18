package com.sparta.home_protector.entity;

import com.sparta.home_protector.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ElementCollection // 다중 값을 가지는 필드이므로 새 테이블을 자동 생성 및 해당 컬럼을 매핑(FK)해주는 애너테이션
    @Column(name = "images", nullable = false)
    private List<String> images;  // 이미지 파일을 서버 또는 외부 스토리지(S3 Bucket)에 업로드하고, 경로를 db에 저장

    @Column(name = "viewCount", nullable = false)
    private int viewCount;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> postLikeList = new ArrayList<>();

    public Post(PostRequestDto postRequestDto, User user
            , List<String> images
    ) {
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.images = images;
        this.user = user;
    }
}
