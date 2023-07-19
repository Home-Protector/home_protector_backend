package com.sparta.home_protector.controller;

import com.sparta.home_protector.dto.LoginRequestDto;
import com.sparta.home_protector.dto.SignupRequestDto;
import com.sparta.home_protector.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto requestDto,
                                         HttpServletResponse response) {
        return userService.signup(requestDto, response);
    }

    // 로그인
    @PostMapping("/user/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse JwtResponse) {
        return userService.login(requestDto, JwtResponse);
    }
}