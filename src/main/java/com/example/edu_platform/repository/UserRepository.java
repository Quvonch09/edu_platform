package com.example.edu_platform.repository;

import com.example.edu_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumber(String phoneNumber);
    @Query(value = "select * from users u join groups_students gs on u.id=gs.students_id",nativeQuery=true)
    List<User> findAllByGroupId(Long groupId);
}
