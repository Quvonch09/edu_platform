package com.example.edu_platform.repository;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.payload.res.ResCEODiagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
