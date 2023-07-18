package com.sparta.home_protector.exception;

import com.sparta.home_protector.dto.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// RestController내에서 발생하는 예외들을 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    // SignupException 처리
    @ExceptionHandler({SignupException.class})
    public ResponseEntity<ExceptionResponseDto> handleException(SignupException ex) {
        ExceptionResponseDto restApiException = new ExceptionResponseDto(
                ex.getMessage(),
                true
        );
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }
    // SignupException 처리
    @ExceptionHandler({LoginException.class})
    public ResponseEntity<ExceptionResponseDto> handleException(LoginException ex) {
        ExceptionResponseDto restApiException = new ExceptionResponseDto(
                ex.getMessage(),
                false
        );
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }
    // MethodArgumentNotValidException (requestDto에서 valid 관련해서 생기는 예외) 처리
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponseDto> handleException(MethodArgumentNotValidException ex) {
        ExceptionResponseDto restApiException = new ExceptionResponseDto(
                ex.getFieldError().getDefaultMessage(),
                false
        );
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }
}
