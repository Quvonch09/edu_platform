package com.example.edu_platform.exception;

import lombok.Getter;
import lombok.Setter;
import com.example.edu_platform.payload.ApiResponse;

@Getter
@Setter
public class NotFoundException extends RuntimeException {

    private ApiResponse apiResponse;
    public NotFoundException(ApiResponse apiResponse) {
        this.apiResponse = apiResponse;
    }
}
