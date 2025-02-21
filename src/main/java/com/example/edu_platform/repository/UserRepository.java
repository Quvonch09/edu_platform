package com.example.edu_platform.repository;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.Role;
import com.example.edu_platform.payload.res.ResCEODiagram;
import com.example.edu_platform.payload.res.ResStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumber(String phoneNumber);
    @Query(value = "select * from users u join groups_students gs on u.id=gs.students_id",nativeQuery=true)
    List<User> findAllByGroupId(Long groupId);

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
                           ON EXTRACT(MONTH FROM u.updated_at) = EXTRACT(MONTH FROM m.month_start)
                               AND EXTRACT(YEAR FROM u.updated_at) = EXTRACT(YEAR FROM m.month_start)
                               AND u.role = 'ROLE_STUDENT'
                               AND u.user_status = 'CHIQIB_KETGAN'
        GROUP BY m.month_start
        ORDER BY m.month_start
""" , nativeQuery = true)
    List<ResCEODiagram> getLeaveStudent();

    boolean existsByPhoneNumberAndRoleAndEnabledTrue(String phone, Role role);


    @Query(value = "select distinct u.* from users u  left join groups g on u.id = g.teacher_id where\n" +
            "                                    (:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%')))\n" +
            "                                    and (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%')))\n" +
            "                                    and (:groupId IS NULL OR g.id = :groupId) and u.role = :role " , nativeQuery = true)
    Page<User> searchUsers(@Param("fullName") String fullName,
                              @Param("phoneNumber") String phoneNumber,
                              @Param("groupId") Long groupId,
                              @Param("role") String role, Pageable pageable);


    @Query(value = """

            SELECT
            COALESCE(COUNT(s.id), 0) AS student_count
        FROM groups g
                 JOIN users u ON g.teacher_id = u.id
                 LEFT JOIN groups_students gu ON gu.group_id = g.id  -- group_user jadvali, bu yerda studentlar va guruhlar bog‘langan
                 LEFT JOIN users s ON s.id = gu.students_id AND s.user_status = 'UQIYABDI'  -- Studentlarning statusi
        WHERE g.teacher_id = :teacherId
          AND g.active = TRUE """ , nativeQuery = true
    )
    Integer countAllByStudent(Long teacherId);



    @Query(value = "SELECT u.id, u.full_name, u.phone_number, g.name as groupName,\n" +
            "       u.created_at, u.age, u.user_status as status, u.parent_phone_number,\n" +
            "       u2.full_name as teacherName,\n" +
            "       CASE\n" +
            "           WHEN extract(month from p.payment_date) = EXTRACT(month from current_date)\n" +
            "               THEN true ELSE false\n" +
            "           END AS hasPaid,\n" +
            "       coalesce(sum(h.ball), 0) score\n" +
            "FROM users u\n" +
            "         JOIN groups_students gsl ON u.id = gsl.students_id\n" +
            "         JOIN groups g ON gsl.group_id = g.id\n" +
            "         LEFT JOIN users u2 on u2.id = g.teacher_id\n" +
            "         LEFT JOIN homework h ON u.id = h.student_id\n" +
            "         LEFT JOIN payment p ON p.student_id = u.id\n" +
            "WHERE (:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%')))\n" +
            "  AND (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%')))\n" +
            "  AND (:userStatus IS NULL OR u.user_status = :userStatus)\n" +
            "  AND (:groupName IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :groupName, '%')))\n" +
            "  AND (:teacherId IS NULL OR g.teacher_id = :teacherId)\n" +
            "  AND (:startAge IS NULL OR u.age >= :startAge)\n" +
            "  AND (:endAge IS NULL OR u.age <= :endAge)\n" +
            "  AND u.role = 'ROLE_STUDENT'\n" +
            "GROUP BY\n" +
            "    u.id,\n" +
            "    u.full_name,\n" +
            "    u.phone_number,\n" +
            "    g.name,\n" +
            "    u.created_at,\n" +
            "    u.age,\n" +
            "    u.user_status,\n" +
            "    u.parent_phone_number,\n" +
            "    u2.full_name,\n" +
            "    p.payment_date\n" +
            "HAVING (:hasPaid IS NULL OR\n" +
            "        (CASE\n" +
            "             WHEN extract(month from p.payment_date) = EXTRACT(month from current_date)\n" +
            "                 THEN true ELSE false\n" +
            "            END) = :hasPaid)",
    nativeQuery = true)
    Page<ResStudent> searchStudents(@Param("fullName") String fullName,
                                    @Param("phoneNumber") String phoneNumber,
                                    @Param("userStatus") String userStatus,
                                    @Param("groupName") String groupName,
                                    @Param("teacherId") Long teacherId,
                                    @Param("startAge") Integer startAge,
                                    @Param("hasPaid") Boolean hasPaid,
                                    @Param("endAge") Integer endAge, Pageable pageable);




    @Query(value = "select coalesce(count(u.*) , 0) from users u join payment p on u.id = p.student_id and u.role = 'ROLE_STUDENT'" +
            "and EXTRACT(month from p.payment_date) = EXTRACT(month from current_date)", nativeQuery = true)
    Integer countStudentsHasPaid();


    List<User> findAllByRole( Role role);

}
