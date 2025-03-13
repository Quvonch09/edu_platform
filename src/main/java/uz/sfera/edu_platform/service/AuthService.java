package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.auth.AuthLogin;
import uz.sfera.edu_platform.payload.auth.ResponseLogin;
import uz.sfera.edu_platform.repository.UserRepository;
import uz.sfera.edu_platform.security.JwtProvider;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;


    public ApiResponse login(AuthLogin authLogin)
    {
        User user = userRepository.findByPhoneNumber(authLogin.getPhoneNumber());
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (!user.isEnabled() || user.isDeleted()){
            return new ApiResponse(ResponseError.ACCESS_DENIED());
        }

        if (passwordEncoder.matches(authLogin.getPassword(), user.getPassword())) {
            String token = jwtProvider.generateToken(authLogin.getPhoneNumber());
            ResponseLogin responseLogin = new ResponseLogin(token, user.getRole().name(), user.getId());
            return new ApiResponse(responseLogin);
        }

        return new ApiResponse(ResponseError.PASSWORD_DID_NOT_MATCH());
    }

}
