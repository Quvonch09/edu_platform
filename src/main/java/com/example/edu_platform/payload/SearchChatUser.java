package com.example.edu_platform.payload;

import java.util.UUID;

public record SearchChatUser(
        Long id,
        String name,
        String phone
) {
}
