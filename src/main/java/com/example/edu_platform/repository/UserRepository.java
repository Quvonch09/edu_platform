package com.example.edu_platform.repository;

import com.example.edu_platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumber(String phoneNumber);

    @Query("select u from User u where u.phoneNumber = ?1 and u.enabled = true")
    User getUserAndEnabledTrue(String phone);

    boolean existsByPhoneNumberAndFullName(String phone, String fullName);


    @Query(value = "select u.* from users u join groups g on u.id = g.teacher_id where " +
            "(:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "and (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) " +
            "and (:groupId IS NULL OR g.id = :groupId)" , nativeQuery = true)
    Page<User> getAllTeachers(@Param("fullName") String fullName,
                              @Param("phoneNumber") String phoneNumber,
                              @Param("groupId") Long groupId, Pageable pageable);
}
