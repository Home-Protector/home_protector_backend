package com.sparta.home_protector.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ExceptionResponseDto {
    private String msg;
    private boolean exist;
    public ExceptionResponseDto(String message , boolean exist) {
        this.msg = message;
        this.exist = exist;
    }
}
