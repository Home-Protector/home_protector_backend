package com.sparta.home_protector.service;

import com.sparta.home_protector.dto.LoginRequestDto;
import com.sparta.home_protector.dto.LoginResponseDto;
import com.sparta.home_protector.dto.SignupRequestDto;
import com.sparta.home_protector.dto.SignupResponseDto;
import com.sparta.home_protector.entity.User;
import com.sparta.home_protector.jwt.JwtUtil;
import com.sparta.home_protector.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //회원가입
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String password = passwordEncoder.encode(requestDto.getPassword());

        //회원 이름 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 username 입니다.");
        }
        // 닉네임 중복 확인
        Optional<User> checkNickname = userRepository.findByUsername(nickname);
        if (checkNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 nickname 입니다.");
        }

        // 사용자 등록
        User user = new User(username, nickname, password);
        userRepository.save(user);

        return new SignupResponseDto("회원가입 성공");
    }

    //    //로그인    security filter에서 하는 방법도 있는데 이게 더 맞는 방법.
    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(() -> //Optional<T>에 orElseThrow 메서드는 결과값이 T로 나온다 (User)
                new IllegalArgumentException("회원을 찾을 수 없습니다."));
        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }
        Long id = user.getId();
        String nickname = user.getNickname();
        // Jwt 토큰 생성, response에 넣기
        String token = jwtUtil.createToken(id, nickname, username);
        // Jwt 헤더에 저장.
        httpServletResponse.addHeader("Authorization", token);

        return new LoginResponseDto("로그인 성공");
    }
}
