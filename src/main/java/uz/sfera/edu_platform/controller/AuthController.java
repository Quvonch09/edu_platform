package uz.sfera.edu_platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.auth.AuthLogin;
import uz.sfera.edu_platform.service.AuthService;

@RestController
@RequestMapping("/api/auth")
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
