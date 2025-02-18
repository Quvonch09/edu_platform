package com.example.edu_platform.repository;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.payload.res.ResStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumber(String phoneNumber);
    @Query(value = "select * from users u join groups_students gs on u.id=gs.students_id",nativeQuery=true)
    List<User> findAllByGroupId(Long groupId);

    @Query("select u from User u where u.phoneNumber = ?1 and u.enabled = true")
    User getUserAndEnabledTrue(String phone);

    boolean existsByPhoneNumberAndFullNameAndRole(String phone, String fullName, Role role);


    @Query(value = "select u.* from users u join groups g on u.id = g.teacher_id where " +
            "(:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "and (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) " +
            "and (:groupId IS NULL OR g.id = :groupId) and role = 'ROLE_TEACHER'" , nativeQuery = true)
    Page<User> searchTeachers(@Param("fullName") String fullName,
                              @Param("phoneNumber") String phoneNumber,
                              @Param("groupId") Long groupId, Pageable pageable);


    @Query(value = "select * from users u where " +
            "(:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "and (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) " +
            "and role = 'ROLE_ADMIN'", nativeQuery = true)
    Page<User> searchAdmins(@Param("fullName") String fullName,
                            @Param("phoneNumber") String phoneNumber, Pageable pageable);


    @Query(value = "SELECT u.full_name, u.phone_number, g.id, u.created_at, u.age, u.user_status, u.parent_phone_number " +
            "FROM users u " +
            "JOIN groups_students gsl ON u.id = gsl.students_id " +
            "JOIN groups g ON gsl.group_id = g.id " +
            "LEFT JOIN payment p ON p.student_id = u.id " +
            "WHERE (:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "AND (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) " +
            "AND (:userStatus IS NULL OR u.user_status = :userStatus) " +
            "AND (:groupName IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :groupName, '%'))) " +
            "AND (:teacherId IS NULL OR g.teacher_id = :teacherId) " +
            "AND (:startAge IS NULL OR u.age >= :startAge) " +
            "AND (:endAge IS NULL OR u.age <= :endAge) " +
            "AND (:date IS NULL OR p.payment_date >= CAST(:date AS TIMESTAMP)) " +
            "AND u.role = 'ROLE_STUDENT'",
            nativeQuery = true)
    Page<ResStudent> searchStudents(@Param("fullName") String fullName,
                                    @Param("phoneNumber") String phoneNumber,
                                    @Param("userStatus") String userStatus,
                                    @Param("groupName") String groupName,
                                    @Param("teacherId") Long teacherId,
                                    @Param("date") LocalDateTime date,
                                    @Param("startAge") Integer startAge,
                                    @Param("endAge") Integer endAge,
                                    Pageable pageable);

}
