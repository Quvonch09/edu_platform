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


    public ApiResponse register(AuthRegister auth)
    {

        User byPhoneNumber = userRepository.findByPhoneNumber(auth.getPhoneNumber());
        if (byPhoneNumber != null) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Phone number"));
        }

        saveUser(auth, Role.ROLE_STUDENT);

        return new ApiResponse("Success");
    }


    public ApiResponse adminSaveUser(AuthRegister auth, Long groupId)
    {

        User byPhoneNumber = userRepository.findByPhoneNumber(auth.getPhoneNumber());
        if (byPhoneNumber != null) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Phone number"));
        }

        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        User user = saveUser(auth, Role.ROLE_STUDENT);

        List<User> students = group.getStudentList();
        students.add(user);
        group.setStudentList(students);
        groupRepository.save(group);
        user.setRole(Role.ROLE_STUDENT);
        userRepository.save(user);


        return new ApiResponse("Success");
    }

    public ApiResponse adminSaveTeacher(AuthRegister auth)
    {

        User byPhoneNumber = userRepository.findByPhoneNumber(auth.getPhoneNumber());
        if (byPhoneNumber != null) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Phone number"));
        }

        saveUser(auth, Role.ROLE_TEACHER);


        return new ApiResponse("Success");
    }


    private User saveUser(AuthRegister auth, Role role)
    {
        User user = User.builder()
                .fullName(auth.getFullName())
                .phoneNumber(auth.getPhoneNumber())
                .password(passwordEncoder.encode(auth.getPassword()))
                .role(role)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        User save = userRepository.save(user);

        return save;
    }
}
