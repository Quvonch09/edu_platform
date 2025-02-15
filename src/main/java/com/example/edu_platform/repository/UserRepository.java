package com.example.edu_platform.repository;

import com.example.edu_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumber(String phoneNumber);

    @Query("select u from User u where u.phoneNumber = ?1 and u.enabled = true")
    User getUserAndEnabledTrue(String phone);
}
