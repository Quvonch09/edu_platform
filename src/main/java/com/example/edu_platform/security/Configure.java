package com.example.edu_platform.security;

import com.example.edu_platform.auditing.ApplicationAuditingAware;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.exception.NotFoundException;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.edu_platform.repository.UserRepository;


@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Configure {

    private final UserRepository repository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = repository.getUserAndEnabledTrue(username);
            if (user != null) {
                return user;
            } else {
                throw new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("User topilmadi")));
            }
        };
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public AuditorAware<Long> auditorAware() {
        return new ApplicationAuditingAware();
    }

}