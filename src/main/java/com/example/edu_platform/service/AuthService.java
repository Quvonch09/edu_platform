package com.example.edu_platform.service;

import com.example.edu_platform.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.edu_platform.entity.Group;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.auth.AuthLogin;
import com.example.edu_platform.payload.auth.AuthRegister;
import com.example.edu_platform.payload.auth.ResponseLogin;
import com.example.edu_platform.repository.GroupRepository;
import com.example.edu_platform.repository.UserRepository;
import com.example.edu_platform.security.JwtProvider;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;


    public ApiResponse login(AuthLogin authLogin)
    {
        User user = userRepository.findByPhoneNumber(authLogin.getPhoneNumber());
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (passwordEncoder.matches(authLogin.getPassword(), user.getPassword())) {
            String token = jwtProvider.generateToken(authLogin.getPhoneNumber());
            ResponseLogin responseLogin = new ResponseLogin(token, user.getRole().name(), user.getId());
            return new ApiResponse(responseLogin);
        }

        return new ApiResponse(ResponseError.PASSWORD_DID_NOT_MATCH());
    }

}
