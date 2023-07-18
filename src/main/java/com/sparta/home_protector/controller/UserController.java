package com.sparta.home_protector.controller;

import com.sparta.home_protector.dto.LoginRequestDto;
import com.sparta.home_protector.dto.LoginResponseDto;
import com.sparta.home_protector.dto.SignupRequestDto;
import com.sparta.home_protector.dto.SignupResponseDto;
import com.sparta.home_protector.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
//@CrossOrigin( origins = "*" , exposedHeaders = "*" )
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return ResponseEntity.ok(userService.signup(requestDto));
    }

    // 로그인
    @PostMapping("/user/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse JwtResponse) {
        return ResponseEntity.ok(userService.login(requestDto, JwtResponse));
    }

}