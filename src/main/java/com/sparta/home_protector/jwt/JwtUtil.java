package com.sparta.home_protector.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;


@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 토큰 유효 시간
    public static final String BEARER_PREFIX = "Bearer ";

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @Value("${jwt.secret.key}")
    private String secretKey;

    // jwt 생성 메서드
    public String createToken(Long id , String nickname){
        Date date = new Date(); // 현재 시간

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(id)) // 토큰(사용자) 식별자 값
                        .claim("nickname" , nickname)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료일
                        .setIssuedAt(date) // 발급일
                        .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, 시크릿 키
                        .compact();
    }

    // jwt 토큰을 받아올때 - substring
    public String substringToken(String token){
        if(StringUtils.hasText(token)&&token.startsWith(BEARER_PREFIX)){ // 토큰이 공백이 아니고 Bearer로 시작할 때
            return token.substring(7);
        }
        throw new NullPointerException("토큰을 찾을 수 없습니다.");
    }

    // jwt 검증 메서드
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token); // key로 token 검증
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfo(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build().parseClaimsJws(token)
                .getBody();
        // Jwt의 구조중 Payload(Body)부분에 토큰에 담긴 정보가 들어있다.
        // 정보의 한 조각을 클레임이라 부르고 key-value의 한 쌍으로 되어있음. 토큰에는 여러개의 클레임들을 넣을 수 있다.
    }
}