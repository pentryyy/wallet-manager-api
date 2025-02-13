package com.pentryyy.wallet_manager_api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.pentryyy.wallet_manager_api.exception.ErrorResponseBuilder;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorResponseBuilder builder = new ErrorResponseBuilder();

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        
        return new ResponseEntity<>(builder.createErrorResponse(
            "Ошибка десериализации: " + e.getMessage()), 
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        
        String errorMessage = "Параметр метода имеет неверный тип: " + 
                              e.getParameter().getParameterName() +
                              ", переданное значение: " + 
                              e.getValue() + " не может быть преобразовано в UUID";
        
        return new ResponseEntity<>(
            builder.createErrorResponse(errorMessage), 
            HttpStatus.BAD_REQUEST);
    }
}