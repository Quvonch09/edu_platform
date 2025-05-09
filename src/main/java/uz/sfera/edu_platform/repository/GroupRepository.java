package uz.sfera.edu_platform.repository;

import jakarta.persistence.QueryHint;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.res.ResCEODiagram;
import uz.sfera.edu_platform.payload.res.ResStudentCount;
import uz.sfera.edu_platform.payload.res.ResStudentRank;
import uz.sfera.edu_platform.payload.res.ResStudentStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByActiveTrue();

    List<Group> findByEndDateLessThanEqualAndActiveTrue(LocalDate date);

    @Query(value = "select u.* from users u join groups_students gs on gs.students_id = u.id where gs.group_id =:groupId", nativeQuery = true)
    List<User> findByGroup(@Param("groupId") Long groupId);

    @Query("SELECT g FROM Group g WHERE g.teacher.id = :teacherId and g.active = true")
    List<Group> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("select  coalesce( count (g) , 0) from Group g where g.active = true ")
    Integer countAllByGroup();

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN groups_students gs ON gs.students_id = u.id " +
            "WHERE gs.group_id = :groupId",
            nativeQuery = true)
    Page<User> findStudentsByGroupId(@Param("groupId") Long groupId,Pageable pageable);

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
            COALESCE(COUNT(g.id), 0) AS count
        FROM months m
                 LEFT JOIN groups g
                           ON EXTRACT(MONTH FROM g.created_at) = EXTRACT(MONTH FROM m.month_start)
                               AND EXTRACT(YEAR FROM g.created_at) = EXTRACT(YEAR FROM m.month_start)
        GROUP BY m.month_start
        ORDER BY m.month_start;
""" , nativeQuery = true)
    List<ResCEODiagram> findByMonthlyStatistic();

    @Query(value = """
        WITH months AS (
            SELECT generate_series(
                DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month',
                DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '3 months',
                '1 month'
            )::DATE AS month_start
        )
        SELECT
            TO_CHAR(m.month_start, 'Month YYYY') AS month,
            COALESCE(COUNT(g.id), 0) AS count 
        FROM months m
        LEFT JOIN groups g
            ON DATE_TRUNC('month', g.end_date) = m.month_start
        GROUP BY m.month_start
        ORDER BY m.month_start;
        
""" , nativeQuery = true)
    List<ResCEODiagram> findByGroupEndDate();

    @Query("select coalesce( count(g) ,0) from  Group g where g.active is true  and g.teacher.id = ?1 ")
    Integer countAllByGroup(Long teacherId);

    @Query(value = """
            WITH lessons_per_group AS (
            SELECT
                g.id,
                COUNT(l) AS total_lessons
            FROM groups g  join groups_students gs on g.id = gs.group_id
            join category c on g.category_id = c.id
            join module m on m.category_id = c.id
            join lesson l on l.module_id = m.id
            where gs.students_id = :studentId and l.deleted = 0 and m.deleted = 0\s
            group by g.id
        ),
             lessons_watched AS (
                 SELECT
                     g.id AS group_id,
                     COUNT(lt.id) AS watched_lessons
                 FROM groups g
                          LEFT JOIN lesson_tracking lt ON g.id = lt.group_id
                 WHERE g.id IN (
                     SELECT group_id FROM groups_students WHERE students_id = :studentId
                 )
                 GROUP BY g.id
             ),
             ranking AS (
                 SELECT
                     s.id,
                     gs.group_id,
                     COALESCE(SUM(h.ball), 0) AS total_score,
                     DENSE_RANK() OVER (PARTITION BY gs.group_id ORDER BY COALESCE(SUM(h.ball), 0) DESC) AS rank_position,
                     COUNT(*) OVER (PARTITION BY gs.group_id) AS total_students
                 FROM groups_students gs
                          JOIN users s ON s.id = gs.students_id
                          LEFT JOIN homework h ON s.id = h.student_id
                 GROUP BY s.id, gs.group_id
             )
        SELECT
            g.name AS groupName,
            g.id AS groupId,
            t.full_name AS teacherName,
        
            -- ✅ Guruhning tugash foizi (progress)
            CASE
                -- Guruh tugagan bo‘lsa, 100% chiqadi
                WHEN g.end_date <= CURRENT_DATE THEN 100.0
                -- Guruh davom etayotgan bo‘lsa, foiz hisoblanadi
                WHEN g.end_date > g.start_date THEN
                    ROUND(
                            100.0 * ((CURRENT_DATE - g.start_date) * 1.0 /
                                     NULLIF((g.end_date - g.start_date), 0)), 2
                    )
                ELSE 100.0
                END AS categoryPercentage,
        
            COALESCE(lw.watched_lessons, 0) || '/' || COALESCE(lp.total_lessons, 0) AS lessonsProgress,
            m.name AS currentModule,
            COALESCE(r.total_score, 0) || '/' || (COALESCE(lp.total_lessons, 0) * 5) AS studentScore,
            COALESCE(r.rank_position, 0) || '/' || COALESCE(r.total_students, 0) AS rankingPosition
        FROM groups g
                 JOIN users t ON g.teacher_id = t.id
                 LEFT JOIN lessons_per_group lp ON lp.id = g.id
                 LEFT JOIN lessons_watched lw ON lw.group_id = g.id
                 LEFT JOIN lesson l ON l.id = (
            SELECT lesson_id FROM lesson_tracking WHERE group_id = g.id ORDER BY created_at DESC LIMIT 1
        )
                 LEFT JOIN module m ON m.id = l.module_id
                 LEFT JOIN ranking r ON r.id= :studentId
        WHERE g.active = TRUE
          AND g.id IN (
            SELECT group_id FROM groups_students WHERE students_id = :studentId
        )
        LIMIT 1;
""", nativeQuery = true)
    ResStudentStatistic findGroupByStudentId(@Param("studentId") Long studentId);

    @Query(value = """

            WITH student_scores AS (
                             SELECT
                                 gs.group_id,
                                 gs.students_id AS student_id,
                                 u.full_name AS student_name,
                                 COALESCE(SUM(h.ball), 0) AS total_score
                             FROM groups_students gs
                             JOIN users u ON gs.students_id = u.id
                             LEFT JOIN homework h ON h.student_id = gs.students_id AND (h.checked = 1)
                             WHERE gs.group_id = (
                                 SELECT group_id FROM groups_students WHERE students_id = :studentId LIMIT 1
                             )
                             GROUP BY gs.group_id, gs.students_id, u.full_name
                         ),
                         ranking AS (
                             SELECT
                                 student_id,
                                 total_score,
                                 RANK() OVER (ORDER BY total_score DESC) AS rank_position
                             FROM student_scores
                         )
                         SELECT
                             ss.group_id AS groupId,
                             ss.student_name AS fullName,
                             ss.total_score AS score,
                             r.rank_position AS rank
                         FROM student_scores ss
                         JOIN ranking r ON ss.student_id = r.student_id
                         ORDER BY r.rank_position;
                         
""", nativeQuery = true)
    Page<ResStudentRank> findAllByStudentRank(@Param("studentId") Long studentId, Pageable pageable);



    @Query(value = """
    WITH student_scores AS (
        SELECT
            gs.group_id,
            gs.students_id AS student_id,
            u.full_name AS student_name,
            COALESCE(SUM(h.ball), 0) AS total_score
        FROM groups_students gs
        JOIN users u ON gs.students_id = u.id
        LEFT JOIN homework h ON h.student_id = u.id AND h.checked = 1
        WHERE gs.group_id IN (
            SELECT group_id FROM groups_students WHERE students_id = :studentId
        )
        GROUP BY gs.group_id, gs.students_id, u.full_name
    ),
    ranking AS (
        SELECT
            group_id,
            student_id,
            total_score,
            RANK() OVER (PARTITION BY group_id ORDER BY total_score DESC) AS rank_position
        FROM student_scores
    )
    SELECT
        ss.student_name as fullName,
        ss.total_score as score,
        r.rank_position as rank
    FROM student_scores ss
    JOIN ranking r ON ss.student_id = r.student_id AND ss.group_id = r.group_id
    ORDER BY r.rank_position
""",
            countQuery = """
    SELECT COUNT(*) FROM groups_students WHERE group_id IN (
        SELECT group_id FROM groups_students WHERE students_id = :studentId
    )
""",
            nativeQuery = true)
    Page<ResStudentRank> findStudentRankingInGroups(@Param("studentId") Long studentId, Pageable pageable);




    @Query(value = "select distinct g.* from groups g join " +
            "groups_students gsl on gsl.students_id = :studentId and gsl.group_id = g.id", nativeQuery = true)
    Optional<Group> findByStudentId(@Param("studentId") Long studentId);

    boolean existsByName(String name);

    @Query(value = """
       SELECT g.* FROM groups g
        join users u on g.teacher_id = u.id
        WHERE (:name IS NULL OR UPPER(g.name) LIKE CONCAT('%', UPPER(:name), '%'))
          AND (:teacherName IS NULL OR UPPER(u.full_name) LIKE CONCAT('%', UPPER(:teacherName), '%'))
          AND (:teacherId IS NULL OR u.id = :teacherId)
          AND (:categoryId IS NULL OR g.category_id = :categoryId)
        ORDER BY g.created_at DESC
    """, nativeQuery = true)
    Page<Group> searchGroup(@Param("name") String name,
                            @Param("teacherName") String teacherName,
                            @Param("categoryId") Long categoryId,
                            @Param("teacherId") Long teacherId,
                            Pageable pageable);


    @Query(value = """
       SELECT g.* FROM groups g
        join users u on g.teacher_id = u.id
        WHERE (:name IS NULL OR UPPER(g.name) LIKE CONCAT('%', UPPER(:name), '%'))
          AND (:teacherName IS NULL OR UPPER(u.full_name) LIKE CONCAT('%', UPPER(:teacherName), '%'))
          AND (:teacherId IS NULL OR u.id = :teacherId)
          AND g.start_date <= :startDate
          AND g.end_date >= :endDate
          AND (:categoryId IS NULL OR g.category_id = :categoryId)
        ORDER BY g.created_at DESC
    """, nativeQuery = true)
    Page<Group> searchGroupDate(@Param("name") String name,
                            @Param("teacherName") String teacherName,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate,
                            @Param("categoryId") Long categoryId,
                            @Param("teacherId") Long teacherId,
                            Pageable pageable);


    @Query(value = """
       SELECT g.* FROM groups g
       JOIN users u ON g.teacher_id = u.id
       WHERE (:name IS NULL OR UPPER(g.name) LIKE CONCAT('%', UPPER(:name), '%'))
         AND (:teacherName IS NULL OR UPPER(u.full_name) LIKE CONCAT('%', UPPER(:teacherName), '%'))
         AND (:teacherId IS NULL OR u.id = :teacherId)
         AND (:categoryId IS NULL OR g.category_id = :categoryId)
         AND (
               (:start = true AND g.start_date <= :date)
               OR (:start = false AND g.end_date >= :date)
             )
       ORDER BY g.created_at DESC
    """, nativeQuery = true)
    Page<Group> searchGroupDate(@Param("name") String name,
                                @Param("teacherName") String teacherName,
                                @Param("categoryId") Long categoryId,
                                @Param("teacherId") Long teacherId,
                                @Param("date") LocalDate date,
                                @Param("start") boolean start,
                                Pageable pageable);


    @Query(value = "SELECT COALESCE(COUNT(*), 0) " +
            "FROM groups g " +
            "JOIN groups_students gs ON g.id = gs.group_id " +
            "JOIN users u ON gs.students_id = u.id " +
            "WHERE u.user_status = 'CHIQIB_KETGAN' " +
            "AND g.id = :groupId",
            nativeQuery = true)
    Integer countGroup(@Param("groupId") Long groupId);

    @Query(value = """
        SELECT
            COALESCE(COUNT(DISTINCT p.student_id), 0) AS paid_students_count
        FROM groups g
                 JOIN groups_students gu ON gu.group_id = g.id
                 JOIN users s ON s.id = gu.students_id AND s.user_status = 'UQIYABDI'
                 JOIN payment p ON p.student_id = s.id AND p.payment_type = 'TUSHUM'
        WHERE g.teacher_id = :teacherId
          AND g.active = TRUE
          AND extract(month from p.payment_date) = extract(month from current_date)
""" , nativeQuery = true)
    Integer countStudentByTeacherId(@Param("teacherId") Long teacherId);

    @Query(value = """

            SELECT
                    g.id AS groupId,
                    g.name AS groupName,
                    COUNT(gu.students_id) AS studentCount
                FROM groups g
                LEFT JOIN groups_students gu ON gu.group_id = g.id
                LEFT JOIN users s ON s.id = gu.students_id AND s.user_status = 'UQIYABDI'
                WHERE g.teacher_id = :teacherId
                    AND g.active = TRUE
                GROUP BY g.id, g.name;
           """ , nativeQuery = true)
    List<ResStudentCount> findAllStudentsByTeacherId(@Param("teacherId") Long teacherId);

    @Query(value = "select coalesce(count(g.*) , 0) from groups g join lesson_tracking lt on g.id = lt.group_id where g.id =:groupId", nativeQuery = true)
    Integer countGroupLessons(@Param("groupId") Long groupId);

    @Query(value = "select g.* from groups g  where g.teacher_id = ?1 ", nativeQuery = true)
    List<Group> findGroup(Long userId);

    @Query(value = "select count(g.*)>0 from groups g join groups_students gs on g.id=gs.group_id where gs.students_id=?1", nativeQuery = true)
    boolean existByStudentId(Long studentId);

    @Transactional
    @Modifying
    @Query(value = "insert into groups_students(group_id, students_id) values (?1, ?2)", nativeQuery = true)
    void addStudentToGroup(Long groupId, Long studentId);
}
