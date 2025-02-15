package com.example.edu_platform.exception;

public class JwtException extends RuntimeException{
    public JwtException(String message) {
        super(message);
    }
}
