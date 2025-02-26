package com.example.edu_platform.payload;

public record SendNachatChat(
        Long userId,
        String message
) {
}
