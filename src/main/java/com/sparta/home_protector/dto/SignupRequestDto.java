package com.sparta.home_protector.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    @Pattern(regexp = "^[A-Za-z0-9]{4,10}$",
            message = "username은 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)만 가능합니다.")
    private String username;

    @Pattern(regexp = "^[가-힣A-Za-z0-9]{4,10}$",
            message = "nickname은 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)만 가능합니다.")
    private String nickname;

    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*\\])(?=.]{8,15}$",
            message = "password는 최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자만 가능합니다.")
    private String password;
}
