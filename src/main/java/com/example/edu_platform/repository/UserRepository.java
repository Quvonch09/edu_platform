package com.example.edu_platform.repository;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.payload.res.ResCEODiagram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumber(String phoneNumber);

    @Query("select u from User u where u.phoneNumber = ?1 and u.enabled = true")
    User getUserAndEnabledTrue(String phone);


    @Query(" select  coalesce(count (u) ,0)  from User u  where  u.role = 'ROLE_TEACHER' and u.enabled = true ")
    Integer countAllByTeacher();

    @Query("select coalesce(count (u),0) from User  u where u.role = 'ROLE_STUDENT' " +
            "and u.enabled = true  and u.userStatus = 'UQIYABDI' " )
    Integer countAllByStudent();


    @Query(" SELECT " +
            "    TO_CHAR(createdAt, 'Month YYYY') AS month, " +
            "    COUNT(*) AS count " +
            "FROM User " +
            "GROUP BY month " +
            "ORDER BY TO_DATE(month, 'Month YYYY') ")
    List<ResCEODiagram> findByYearlyStatistic();

    boolean existsByPhoneNumberAndFullName(String phone, String fullName);


    @Query(value = "select u.* from users u join groups g on u.id = g.teacher_id where " +
            "(:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "and (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) " +
            "and (:groupId IS NULL OR g.id = :groupId)" , nativeQuery = true)
    Page<User> getAllTeachers(@Param("fullName") String fullName,
                              @Param("phoneNumber") String phoneNumber,
                              @Param("groupId") Long groupId, Pageable pageable);
}
