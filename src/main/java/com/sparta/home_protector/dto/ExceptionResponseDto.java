package com.sparta.home_protector.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ExceptionResponseDto {
    private String msg;
    public ExceptionResponseDto(String message ) {
        this.msg = message;
    }
}
