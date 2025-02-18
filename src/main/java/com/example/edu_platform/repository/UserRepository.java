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


    @Query(value = """
       WITH months AS (
           SELECT generate_series(
                          DATE_TRUNC('year', CURRENT_DATE),
                          DATE_TRUNC('month', CURRENT_DATE),
                          '1 month'
                  )::DATE AS month_start
       )
       SELECT
           TO_CHAR(m.month_start, 'Month') AS month,
           COALESCE(COUNT(u.id), 0) AS count
       FROM months m
                LEFT JOIN users u
                          ON EXTRACT(MONTH FROM u.created_at) = EXTRACT(MONTH FROM m.month_start)
                              AND EXTRACT(YEAR FROM u.created_at) = EXTRACT(YEAR FROM m.month_start)
                              AND u.role = 'ROLE_STUDENT'
       GROUP BY m.month_start
       ORDER BY m.month_start;
""" , nativeQuery = true)
    List<ResCEODiagram> getCEODiagrams();


    @Query(value = """
        WITH months AS (
            SELECT generate_series(
                           DATE_TRUNC('year', CURRENT_DATE),
                           DATE_TRUNC('month', CURRENT_DATE),
                           '1 month'
                   )::DATE AS month_start
        )
        SELECT
            TO_CHAR(m.month_start, 'Month') AS month,
            COALESCE(COUNT(u.id), 0) AS count
        FROM months m
                 LEFT JOIN users u
                           ON EXTRACT(MONTH FROM u.update_at) = EXTRACT(MONTH FROM m.month_start)
                               AND EXTRACT(YEAR FROM u.update_at) = EXTRACT(YEAR FROM m.month_start)
                               AND u.role = 'ROLE_STUDENT'
                               AND u.user_status = 'CHIQIB_KETGAN'
        GROUP BY m.month_start
        ORDER BY m.month_start
""" , nativeQuery = true)
    List<ResCEODiagram> getLeaveStudent();

    boolean existsByPhoneNumberAndFullName(String phone, String fullName);


    @Query(value = "select u.* from users u join groups g on u.id = g.teacher_id where " +
            "(:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "and (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) " +
            "and (:groupId IS NULL OR g.id = :groupId)" , nativeQuery = true)
    Page<User> getAllTeachers(@Param("fullName") String fullName,
                              @Param("phoneNumber") String phoneNumber,
                              @Param("groupId") Long groupId, Pageable pageable);


    @Query(value = """

            SELECT
            COALESCE(COUNT(s.id), 0) AS student_count
        FROM groups g
                 JOIN users u ON g.teacher_id = u.id
                 LEFT JOIN groups_student_list gu ON gu.group_id = g.id  -- group_user jadvali, bu yerda studentlar va guruhlar bog‘langan
                 LEFT JOIN users s ON s.id = gu.student_list_id AND s.user_status = 'UQIYABDI'  -- Studentlarning statusi
        WHERE g.teacher_id = :teacherId
          AND g.active = TRUE """ , nativeQuery = true
    )
    Integer countAllByStudent(Long teacherId);



}
