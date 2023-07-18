package com.sparta.home_protector.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String msg;

    public LoginResponseDto(String msg) {
        this.msg = msg;

    }
}
