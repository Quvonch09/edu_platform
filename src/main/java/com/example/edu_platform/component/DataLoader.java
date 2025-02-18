package com.example.edu_platform.component;

import com.example.edu_platform.entity.DayOfWeek;
import com.example.edu_platform.entity.enums.WeekDay;
import com.example.edu_platform.repository.DayOfWeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DayOfWeekRepository dayOfWeekRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Override
    public void run(String... args)  {
        if (ddl.equals("create-drop") || ddl.equals("create")) {
            User newUser = new User();
            newUser.setFullName("Admin admin");
            newUser.setPassword(passwordEncoder.encode("12345"));
            newUser.setRole(Role.ROLE_CEO);
            newUser.setPhoneNumber("998901234567");
            newUser.setEnabled(true);
            newUser.setAccountNonExpired(true);
            newUser.setAccountNonLocked(true);
            newUser.setCredentialsNonExpired(true);
            userRepository.save(newUser);

            DayOfWeek monday = new DayOfWeek();
            monday.setDayOfWeek(WeekDay.MONDAY);
            dayOfWeekRepository.save(monday);
            DayOfWeek tuesday = new DayOfWeek();
            tuesday.setDayOfWeek(WeekDay.TUESDAY);
            dayOfWeekRepository.save(tuesday);
            DayOfWeek wednesday = new DayOfWeek();
            wednesday.setDayOfWeek(WeekDay.WEDNESDAY);
            dayOfWeekRepository.save(wednesday);
            DayOfWeek thursday = new DayOfWeek();
            thursday.setDayOfWeek(WeekDay.THURSDAY);
            dayOfWeekRepository.save(thursday);
            DayOfWeek friday = new DayOfWeek();
            friday.setDayOfWeek(WeekDay.FRIDAY);
            dayOfWeekRepository.save(friday);
            DayOfWeek saturday = new DayOfWeek();
            saturday.setDayOfWeek(WeekDay.SATURDAY);
            dayOfWeekRepository.save(saturday);
            DayOfWeek sunday = new DayOfWeek();
            sunday.setDayOfWeek(WeekDay.SUNDAY);
            dayOfWeekRepository.save(sunday);
        }
    }
}
