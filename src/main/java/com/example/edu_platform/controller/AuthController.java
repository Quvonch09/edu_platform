package com.example.edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.auth.AuthLogin;
import com.example.edu_platform.payload.auth.AuthRegister;
import com.example.edu_platform.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> logIn(
            @Valid @RequestBody AuthLogin authLogin
    ){
        return ResponseEntity.ok(authService.login(authLogin));
    }

}
