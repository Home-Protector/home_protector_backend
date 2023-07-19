package com.sparta.home_protector.dto;

import lombok.Getter;

@Getter
public class SignupResponseDto {
    private String msg;
    private boolean valid;
    public SignupResponseDto(String msg , boolean valid) {
        this.msg = msg;
        this.valid = valid;
    }
}
