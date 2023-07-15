package com.sparta.home_protector.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.home_protector.dto.PostRequestDto;
import com.sparta.home_protector.entity.Post;
import com.sparta.home_protector.entity.User;
import com.sparta.home_protector.repository.PostRepository;
import com.sparta.home_protector.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AmazonS3 amazonS3;
    private final String bucket;

    public PostService(PostRepository postRepository, UserRepository userRepository, AmazonS3 amazonS3, String bucket) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    //  게시글 작성 비즈니스 로직
    public ResponseEntity<String> createPost(PostRequestDto postRequestDto, Long tokenId) {
        // JWT Id로 해당 USER 객체 생성
        User user = userRepository.findById(tokenId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 사용자가 전송한 파일 및 파일명 확인
        MultipartFile file = postRequestDto.getImage();
        String fileName = file.getOriginalFilename();

        // 파일 null check
        if (file == null || file.isEmpty()){
            throw new IllegalArgumentException("이미지가 존재하지 않습니다.");
        }

        String path = Paths.get(fileName).toString(); // 원본 파일명으로 파일 경로 생성
        String extension = StringUtils.getFilenameExtension(path); // 확장자명

        // 파일 확장자 null check
        if (extension == null){
            throw new IllegalArgumentException("파일의 확장자가 잘못되었습니다");
        }

        // 파일 확장자 지원 여부 check
        List<String> fileExtensions = Arrays.asList("jpg", "png", "webp", "heif", "heic", "gif");

        if (!fileExtensions.contains(extension.toLowerCase())){
            throw new IllegalArgumentException("지원되지 않는 확장자 형식입니다.");
        }

        // 파일 크기 검증
        long maxSize = 20 * 1024 * 1024; // 20MB
        long fileSize = file.getSize();
        if (fileSize > maxSize) throw new IllegalArgumentException("파일의 크기가 기준보다 초과되었습니다");

        uploadFileToS3(file, fileName); // S3에 이미지 저장(파일, 파일명)
        String S3ObjectUrl = amazonS3.getUrl(bucket, fileName).toString(); // 해당 이미지의 URL(bucket 이름, 파일명)

        Post post = new Post(postRequestDto, user, S3ObjectUrl);
        postRepository.save(post);

        return ResponseEntity.ok("게시글 등록 완료!");
    }

    // 이미지 파일 업로드 메서드
    private void uploadFileToS3(MultipartFile file, String filename){
        try {
            // 업로드할 파일의 메타데이터 생성(파일 크기.byte)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());

            PutObjectRequest request = new PutObjectRequest(bucket, filename, file.getInputStream(), metadata);
            amazonS3.putObject(request);
        } catch (IOException e){
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.");
        }
    }
}
