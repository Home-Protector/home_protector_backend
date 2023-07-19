package com.sparta.home_protector.service;
import java.nio.file.AccessDeniedException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.home_protector.dto.PostRequestDto;
import com.sparta.home_protector.dto.PostResponseDto;
import com.sparta.home_protector.entity.Post;
import com.sparta.home_protector.entity.PostLike;
import com.sparta.home_protector.entity.User;
import com.sparta.home_protector.jwt.JwtUtil;
import com.sparta.home_protector.repository.PostLikeRepository;
import com.sparta.home_protector.repository.PostRepository;
import com.sparta.home_protector.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Paths;
import java.util.*;

@Slf4j(topic = "Post 서비스")
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final AmazonS3 amazonS3;
    private final String bucket;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostLikeRepository postLikeRepository, AmazonS3 amazonS3, String bucket, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
        this.jwtUtil = jwtUtil;
    }

    // 게시글 전체 조회 비즈니스 로직
    public List<PostResponseDto> getAllPost() {
        List<PostResponseDto> allPost = postRepository.findAll()
                .stream().map(PostResponseDto::new).toList();
        return allPost;
    }

    // 게시글 상세 조회 비즈니스 로직 (조회수 로직 포함)
    public PostResponseDto getPostDetail(Long postId, HttpServletRequest request, HttpServletResponse response) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 게시글입니다."));
        getViewCount(postId, request, response); // 조회수 처리 메서드
        return new PostResponseDto(post);
    }

    //  게시글 작성 비즈니스 로직
    public ResponseEntity<String> createPost(PostRequestDto postRequestDto, Long userId) {
        // JWT Id로 해당 USER 객체 생성
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));

        // 클라이언트가 전송한 이미지 파일
        List<MultipartFile> files = postRequestDto.getImages();

        // 파일 검증(null, 크기, 확장자)
        if (!validateFile(files)) {
            throw new IllegalArgumentException("파일 검증에 실패했습니다.");
        }

        // S3에 이미지 저장 후 해당 이미지 파일의 url을 list<String(key),String>로 반환
        Map<String, String> S3ObjectUrl = uploadFileToS3(files);

        // Entity 객체 생성 후 DB에 저장
        Post post = new Post(postRequestDto, user, S3ObjectUrl);
        postRepository.save(post);

        return ResponseEntity.ok("게시글 등록 완료!");
    }

    // 게시글 수정 비즈니스 로직
    @Transactional
    public ResponseEntity<String> updatePost(PostRequestDto postRequestDto, Long postId, Long userId) throws AccessDeniedException {
        // JWT Id로 해당 USER 객체 생성
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));


        // 기존 게시글 Entity
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 게시글입니다."));

        // 게시글 수정 권한 확인
        if (!user.getUsername().equals(post.getUser().getUsername())) {
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다!");
        }

        // 이미지 파일이 있을 경우만 S3 객체 수정 로직 수행
        if (postRequestDto.getImages() != null && !postRequestDto.getImages().isEmpty()) {
            // 클라이언트가 전송한 이미지 파일
            List<MultipartFile> files = postRequestDto.getImages();

            // 파일 검증(null, 크기, 확장자)
            if (!validateFile(files)) {
                throw new IllegalArgumentException("파일 검증에 실패했습니다.");
            }

            // 기존 Post에 저장되어 있던 이미지 파일의 UUID를 리스트로 반환
            Set<String> originKeys = post.getImages().keySet();

            // 기존 Post에 저장된 S3 이미지 객체를 모두 삭제 후 클라이언트가 보낸 새 파일 저장
            Map<String, String> modiefiedUrl = updateFileToS3(originKeys, files);
            post.update(postRequestDto, user, modiefiedUrl);
        }

        // Case : 이미지를 제외한 입력 데이터만 수정
        post.update(postRequestDto, user);
        return ResponseEntity.ok("게시글 수정 완료!");
    }

    // 게시글 삭제 비즈니스 로직
    public ResponseEntity<String> deletePost(Long postId, Long userId) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 게시글입니다."));

        if (!user.getUsername().equals(post.getUser().getUsername())) {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다!");
        }

        postRepository.delete(post);

        return ResponseEntity.ok("게시글 삭제 완료!");
    }

    // 이미지 파일 업로드(S3) 메서드
    private Map<String, String> uploadFileToS3(List<MultipartFile> files) {
        Map<String, String> images = new LinkedHashMap<>();

        // 새 S3 객체 업로드
        files.stream().forEachOrdered(file -> {
            String filename = file.getOriginalFilename(); // 파일의 원본명
            String extension = StringUtils.getFilenameExtension(Paths.get(filename).toString()); // 확장자명
            String fileUuid = generateUniqueFilename(extension); // 해당 파일의 고유한 이름

            // 업로드할 파일의 메타데이터 생성(확장자 / 파일 크기.byte)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + extension);
            metadata.setContentLength(file.getSize());

            // 요청 객체 생성(버킷명, 파일명, 스트림, 메타정보)
            PutObjectRequest request = null;
            try {
                request = new PutObjectRequest(bucket, fileUuid, file.getInputStream(), metadata);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // S3 버킷에 PUT(등록 요청)
            amazonS3.putObject(request);

            // 해당 객체의 Url 값을 String으로 images리스트에 저장
            images.put(fileUuid, amazonS3.getUrl(bucket, fileUuid).toString());
        });
        return images;
    }

    // 이미지 파일 수정(S3) 메서드
    private Map<String, String> updateFileToS3(Set<String> originKeys, List<MultipartFile> files) {
        // 기존 저장해둔 S3 객체 삭제
        for (String key : originKeys) {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);
            amazonS3.deleteObject(deleteObjectRequest);
        }

        return uploadFileToS3(files);
    }


    // 업로드한 파일으로 고유 파일명을 생성해주는 메서드(이미지 파일 중복 문제)
    private String generateUniqueFilename(String extension) {
        String timestamp = String.valueOf(System.currentTimeMillis()); // 업로드 시간
        String randomUuid = UUID.randomUUID().toString(); // 랜덤한 고유 String 생성
        return timestamp + "_" + randomUuid + "." + extension;
    }


    // 파일 검증 메서드(null check, 파일 확장자, 파일 크기)
    private boolean validateFile(List<MultipartFile> files) {
        // 지원하는 파일 확장자 리스트
        List<String> fileExtensions = Arrays.asList("jpg", "png", "webp", "heif", "heic", "gif");

        files.stream().forEachOrdered(file -> {
            // 파일 null check
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("이미지가 존재하지 않습니다.");
            }

            String path = Paths.get(file.getOriginalFilename()).toString(); // 원본 파일명으로 파일 경로 생성
            String extension = StringUtils.getFilenameExtension(path); // 확장자명

            // 파일 확장자 null check
            if (extension == null) {
                throw new IllegalArgumentException("파일의 확장자가 잘못되었습니다");
            }

            // 파일 확장자 검증
            if (!fileExtensions.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("지원되지 않는 확장자 형식입니다.");
            }

            // 파일 크기 검증
            long maxSize = 20 * 1024 * 1024; // 20MB
            long fileSize = file.getSize();

            if (fileSize > maxSize) {
                throw new IllegalArgumentException("파일의 크기가 기준보다 초과되었습니다");
            }
        });
        return true;
    }

    // 게시글 좋아요 비즈니스 로직
    @Transactional
    public ResponseEntity<String> likePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 게시글입니다."));
        PostLike postLike = postLikeRepository.findByPostAndUser(post, user).orElse(null);
        if (postLike == null) {
            PostLike newPostLike = new PostLike(user, post);
            postLikeRepository.save(newPostLike);
            return ResponseEntity.ok("좋아요 성공");
        } else {
            postLikeRepository.delete(postLike);
            return ResponseEntity.ok("좋아요 취소");
        }
    }

    // 조회수 처리 메서드, 토큰을 이용해서 사용자마다 다른 쿠키가 생성되도록 해서 중복 방지 및 사용자 구분
    // 게시글 조회수 비즈니스 로직
    public void getViewCount(Long postId,
                             HttpServletRequest request, HttpServletResponse response){

        String tokenValue =  jwtUtil.getTokenFromRequest(request);

        String token = "";

        // 토큰 유무와 형태 확인, 정상이면 substring 후 검증 및 반환, 아니라면 비회원으로 간주하고 비회원용 쿠키 넣어주고 반환
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith("Bearer ")) {
            token = tokenValue.substring(7);

            // 토큰 검증 실패시에도 비회원으로 간주 후 반환
            if (!jwtUtil.validateToken(token)) {
                nonMember(postId, request, response);
                return;
            }
        }else { // 토큰이 null이거나 형태가 정상적이지 않은 경우
            nonMember(postId, request, response);
            return;
        }

        // 토큰에서 유저 id 가져와서 user 정보 조회
        Claims info = jwtUtil.getUserInfo(token);

        // 토큰에서 username 가져오기
        String username = info.get("username",String.class);

        // postId와 해당 사용자 username를 넣어준 cookieName 생성
        String cookieName = "viewed_post_" + postId + "_" + username;

        // request에서 쿠키 가져오기
        Cookie[] cookies = request.getCookies();

        // 쿠키가 존재하는 경우
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 쿠키 중 현재 유저의 해당 post 쿠키가 존재한다면 이미 조회한 것이므로 종료하고 반환
                if (cookie.getName().equals(cookieName)) {
                    return;
                }
            }
        }

        // 24시간 내로 해당 post를 조회한 적이 없는 유저인 경우 쿠키 생성
        increaseViewCount(postId); // 조회수 증가
        Cookie newCookie = new Cookie(cookieName, "true"); // 새 쿠키 생성
        newCookie.setMaxAge(60 * 60 * 24); // 쿠키 유효시간 24시간
        response.addCookie(newCookie); // response에 새 쿠키 추가
    }

    // 비회원용 토큰 생성 메서드
    public void nonMember(Long postId, HttpServletRequest request, HttpServletResponse response){
        // request에서 쿠키 가져오기
        Cookie[] cookies = request.getCookies();

        String cookieName = "viewed_post_" + postId + "_nonmember";

        // 쿠키 중 비회원용 쿠키가 존재한다면 이미 조회한 것이므로 종료
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return;
                }
            }
        }

        // postId를 넣어준 비회원용 cookieName 생성하고 반환 및 종료
        increaseViewCount(postId); // 조회수 증가
        Cookie newCookie = new Cookie(cookieName, "true"); // 새 쿠키 생성
        newCookie.setMaxAge(60 * 60 * 24); // 쿠키 유효시간 24시간
        response.addCookie(newCookie); // response에 새 쿠키 추가
    }

    // 조회수 증가 메서드
    public void increaseViewCount(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }
}
