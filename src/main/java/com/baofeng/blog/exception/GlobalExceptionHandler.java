package com.baofeng.blog.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.baofeng.blog.vo.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //框架自动抛出异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(ApiResponse.error(400, errorMessage));
    }
    //自定义重复异常
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateUserException(DuplicateUserException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(400, ex.getMessage()));
    }
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(AuthException ex){
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getStatusCode(), ex.getMessage()));
    }
    

} 