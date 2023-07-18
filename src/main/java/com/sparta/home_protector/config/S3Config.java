//package com.sparta.home_protector.config;
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration // Spring 컨테이너가 해당 클래스를 로드하고 애플리케이션의 구성을 구성하고, Bean 등록됨
//public class S3Config {
//    @Value("${cloud.aws.credentials.accessKey}")
//    private String accessKey;
//    @Value("${cloud.aws.credentials.secretKey}")
//    private String secretKey;
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @Bean
//    public AmazonS3 amazonS3Client() {
//        // AWS 액세스 키와 비밀 키를 사용하여 인증 정보 객체 생성
//        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//
//        // AmazonS3 클라이언트를 빌드하여 생성
//        return AmazonS3ClientBuilder
//                .standard() // S3 클라이언트 빌더를 생성
//                .withCredentials(new AWSStaticCredentialsProvider(credentials)) // 인증 정보를 클라이언트에게 제공
//                .withRegion(region) // 사용할 리전 설정
//                .build(); // AmazonS3 클라이언트 생성
//    }
//
//    @Bean
//    public String Bucket(){
//        return bucket;
//    }
//}
