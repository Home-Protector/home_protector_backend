package com.sparta.home_protector.dto;

import lombok.Getter;

@Getter
public class SignupResponseDto {
    private String msg;

    public SignupResponseDto( String msg) {
        this.msg = msg;
    }
}
