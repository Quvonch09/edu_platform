package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.payload.res.ResCEODiagram;
import uz.sfera.edu_platform.payload.res.ResStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumber(String phoneNumber);

    @Query(value = "select g.teacher_id from groups g join groups_students gs on g.id=gs.group_id where gs.students_id=?1",
            nativeQuery = true)
    Long getTeacherId(Long studentId);

    @Query(value = "select * from users u join groups_students gs on u.id=gs.students_id where gs.group_id = :groupId",nativeQuery=true)
    List<User> findAllByGroupId(Long groupId);

    @Query("select u from User u where u.phoneNumber = ?1 and u.enabled = true")
    User getUserAndEnabledTrue(String phone);

    @Query(" select  coalesce(count (u) ,0)  from User u  where  u.role = 'ROLE_TEACHER' and u.enabled = true ")
    Integer countAllByTeacher();

    @Query(value = "select count(*) from users where role = 'ROLE_STUDENT' and enabled = true and user_status = 'UQIYAPDI'", nativeQuery = true )
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

    boolean existsByPhoneNumberAndEnabledIsTrue(String phone);
    boolean existsByPhoneNumberAndEnabledIsTrueAndIdNot(String phone, Long id);

    @Query(value = """
    SELECT DISTINCT u.* 
    FROM users u  
    LEFT JOIN groups g ON u.id = g.teacher_id 
    LEFT JOIN users_categories uc ON uc.user_id = u.id
    WHERE (:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :fullName, '%')))
      AND (:phoneNumber IS NULL OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', :phoneNumber, '%')))
      AND (:categoryId IS NULL OR uc.categories_id = :categoryId)
      AND u.role = :role
      AND u.deleted = false
    ORDER BY u.created_at DESC
    """, nativeQuery = true)
    Page<User> searchUsers(@Param("fullName") String fullName,
                           @Param("phoneNumber") String phoneNumber,
                           @Param("categoryId") Long categoryId,
                           @Param("role") String role,
                           Pageable pageable);



    @Query(value = """

            SELECT
            COALESCE(COUNT(s.id), 0) AS student_count
        FROM groups g
                 JOIN users u ON g.teacher_id = u.id
                 LEFT JOIN groups_students gu ON gu.group_id = g.id  -- group_user jadvali, bu yerda studentlar va guruhlar bog‘langan
                 LEFT JOIN users s ON s.id = gu.students_id AND s.user_status = 'UQIYAPDI'  -- Studentlarning statusi
        WHERE g.teacher_id = :teacherId
          AND g.active = TRUE""" , nativeQuery = true
    )
    Integer countAllByStudent(Long teacherId);

    @Query(
            value = """
SELECT
    u.id,
    u.full_name,
    u.phone_number,
    g.name AS groupName,
    g.id AS groupId,
    u.created_at,
    u.age,
    u.user_status AS status,
    u.departure_date as departureDate,
    u.departure_description as departureDescription,
    u.parent_phone_number,
    u2.full_name AS teacherName,
    CASE
        WHEN EXISTS (
            SELECT 1 FROM income inc
            WHERE inc.student_id = u.id AND inc.paid = TRUE
              AND EXTRACT(MONTH FROM inc.payment_date) = EXTRACT(MONTH FROM CURRENT_DATE)
              AND EXTRACT(YEAR FROM inc.payment_date) = EXTRACT(YEAR FROM CURRENT_DATE)
        ) THEN TRUE
        ELSE FALSE
        END AS hasPaid,
    COALESCE(SUM(h.ball), 0) AS score
FROM users u
         JOIN groups_students gsl ON u.id = gsl.students_id
         JOIN groups g ON gsl.group_id = g.id
         LEFT JOIN users u2 ON u2.id = g.teacher_id
         LEFT JOIN homework h ON u.id = h.student_id
         LEFT JOIN income inc ON inc.student_id = u.id
WHERE
    (:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', COALESCE(:fullName, ''), '%')))
  AND (COALESCE(:phoneNumber, '') = '' OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', COALESCE(:phoneNumber, ''), '%')))
  AND (COALESCE(:userStatus, '') = '' OR u.user_status = :userStatus)
  AND (COALESCE(:groupName, '') = '' OR LOWER(g.name) LIKE LOWER(CONCAT('%', COALESCE(:groupName, ''), '%')))
  AND (:teacherId IS NULL OR g.teacher_id = :teacherId)
  AND (:startAge IS NULL OR u.age >= :startAge)
  AND (:endAge IS NULL OR u.age <= :endAge)
  AND u.role = 'ROLE_STUDENT'
  AND (
    :hasPaid IS NULL OR EXISTS (
        SELECT 1 FROM income inc2
        WHERE inc2.student_id = u.id
          AND inc2.paid = CASE WHEN :hasPaid THEN TRUE ELSE FALSE END
          AND EXTRACT(MONTH FROM inc2.payment_date) = EXTRACT(MONTH FROM CURRENT_DATE)
          AND EXTRACT(YEAR FROM inc2.payment_date) = EXTRACT(YEAR FROM CURRENT_DATE)
    ))
GROUP BY
    u.id, u.full_name, u.phone_number, g.name, g.id,
    u.created_at, u.age, u.user_status, u.departure_date, u.departure_description, u.parent_phone_number, u2.full_name
ORDER BY u.created_at DESC
""",
            countQuery = """
    SELECT COUNT(DISTINCT u.id)
    FROM users u
    JOIN groups_students gsl ON u.id = gsl.students_id
    JOIN groups g ON gsl.group_id = g.id
    LEFT JOIN users u2 ON u2.id = g.teacher_id
    LEFT JOIN income inc ON inc.student_id = u.id
    WHERE
        (:fullName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', COALESCE(:fullName, ''), '%')))
        AND (COALESCE(:phoneNumber, '') = '' OR LOWER(u.phone_number) LIKE LOWER(CONCAT('%', COALESCE(:phoneNumber, ''), '%')))
        AND (COALESCE(:userStatus, '') = '' OR u.user_status = :userStatus)
        AND (COALESCE(:groupName, '') = '' OR LOWER(g.name) LIKE LOWER(CONCAT('%', COALESCE(:groupName, ''), '%')))
        AND (:teacherId IS NULL OR g.teacher_id = :teacherId)
        AND (:startAge IS NULL OR u.age >= :startAge)
        AND (:endAge IS NULL OR u.age <= :endAge)
        AND u.role = 'ROLE_STUDENT'
        AND (
    :hasPaid IS NULL OR EXISTS (
        SELECT 1 FROM income inc2
        WHERE inc2.student_id = u.id
          AND inc2.paid = CASE WHEN :hasPaid THEN TRUE ELSE FALSE END
          AND EXTRACT(MONTH FROM inc2.payment_date) = EXTRACT(MONTH FROM CURRENT_DATE)
          AND EXTRACT(YEAR FROM inc2.payment_date) = EXTRACT(YEAR FROM CURRENT_DATE)
    ))
""",
            nativeQuery = true)

    Page<ResStudent> searchStudents(
            @Param("fullName") String fullName,
            @Param("phoneNumber") String phoneNumber,
            @Param("userStatus") String userStatus,
            @Param("groupName") String groupName,
            @Param("teacherId") Long teacherId,
            @Param("startAge") Integer startAge,
            @Param("endAge") Integer endAge,
            @Param("hasPaid") Boolean hasPaid,
            Pageable pageable);


    @Query(value = "select coalesce(count(u.*) , 0) from users u join income p on u.id = p.student_id and u.role = 'ROLE_STUDENT'" +
            "and EXTRACT(month from p.payment_date) = EXTRACT(month from current_date) where p.paid = true", nativeQuery = true)
    Integer countStudentsHasPaid();

    List<User> findAllByRoleAndDeletedFalse ( Role role);

    @Query("select u from User u where (u.fullName like :fullName or u.phoneNumber = :phone) and u.role = :roleName")
    List<User> searchForChat(@Param("fullName") String fullName,
                             @Param("phone") String phone,
                             @Param("roleName") String roleName);

    @Query(value = """
            SELECT s.*
            FROM users s
            JOIN groups_students gs ON s.id = gs.students_id
            JOIN groups g ON gs.group_id = g.id
            WHERE g.teacher_id = :teacherId and g.active = true and s.user_status = 'UQIYAPDI' and s.deleted = false
         """ , nativeQuery = true
    )
    List<User> searchForUsers(@Param("teacherId") Long teacherId);

    @Query(value = """

            SELECT
                u.*
             FROM groups g
                 JOIN users u ON g.teacher_id = u.id
                 LEFT JOIN groups_students gu ON gu.group_id = g.id  -- group_user jadvali, bu yerda studentlar va guruhlar bog‘langan
        WHERE gu.students_id = :studentId
          AND g.active = TRUE""" , nativeQuery = true
    )
    List<User> searchForTeacher(@Param("studentId") Long studentId);


    @Query(value = """
    select u.* from users u join users_categories uc on u.id = uc.user_id where uc.categories_id = :categoryId
""", nativeQuery = true)
    List<User> findAllByCategories(@Param("categoryId") Long categoryId);
}
