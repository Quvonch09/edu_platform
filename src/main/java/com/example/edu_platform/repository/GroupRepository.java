package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Group;
import com.example.edu_platform.payload.res.ResCEODiagram;
import com.example.edu_platform.payload.res.ResStudentStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("select  coalesce( count (g) , 0) from Group g where g.active = true ")
    Integer countAllByGroup();
    Integer countByTeacherId(Long teacherId);


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
        lt.group_id,
        COUNT(*) AS total_lessons
    FROM lesson_tracking lt
    GROUP BY lt.group_id
),
     lessons_watched AS (
         SELECT
             g.id AS group_id,
             COUNT(lt.id) AS watched_lessons
         FROM groups g
                  LEFT JOIN lesson_tracking lt ON g.id = lt.group_id
         WHERE g.id IN (
             SELECT group_id FROM groups_student_list WHERE student_list_id = :studentId
         )
         GROUP BY g.id
     ),
     student_scores AS (
         SELECT
             g.id AS group_id,
             COALESCE(SUM(h.ball), 0) AS total_score
         FROM groups g
                  LEFT JOIN homework h ON h.student_id = :studentId
         WHERE h.checked = TRUE
         GROUP BY g.id
     ),
     ranking AS (
         SELECT
             student_id,
             SUM(ball) AS total_score,
             RANK() OVER (ORDER BY SUM(ball) DESC) AS rank_position,
             COUNT(*) OVER () AS total_students
         FROM homework
         WHERE checked = TRUE
         GROUP BY student_id
     )
SELECT
    g.name AS groupName,
    t.full_name AS teacherName,

    -- ✅ Guruhning tugash foizi (progress)
    CASE
        WHEN g.end_date > g.created_at THEN
            ROUND(100.0 * (DATE_PART('day', CURRENT_DATE - g.created_at) /
                           NULLIF(DATE_PART('day', g.end_date - g.created_at), 0))::numeric, 2)
        ELSE 100
        END AS categoryPercentage,

    COALESCE(lw.watched_lessons, 0) || '/' || COALESCE(lp.total_lessons, 0) AS lessonsProgress,
    m.name AS currentModule,
    COALESCE(ss.total_score, 0) || '/' || (COALESCE(lp.total_lessons, 0) * 5) AS studentScore,
    COALESCE(r.rank_position, 0) || '/' || COALESCE(r.total_students, 0) AS rankingPosition
FROM groups g
         JOIN users t ON g.teacher_id = t.id
         LEFT JOIN lessons_per_group lp ON lp.group_id = g.id
         LEFT JOIN lessons_watched lw ON lw.group_id = g.id
         LEFT JOIN lesson l ON l.id = (
    SELECT lesson_id FROM lesson_tracking WHERE group_id = g.id ORDER BY created_at DESC LIMIT 1
)
         LEFT JOIN module m ON m.id = l.module_id
         LEFT JOIN student_scores ss ON ss.group_id = g.id
         LEFT JOIN ranking r ON r.student_id = :studentId
WHERE g.active = TRUE
  AND g.id IN (
    SELECT group_id FROM groups_student_list WHERE student_list_id = :studentId
)
LIMIT 1;
""", nativeQuery = true)
    ResStudentStatistic findGroupByStudentId(@Param("studentId") Long studentId);





    @Query(value = """
WITH student_scores AS (
    SELECT
        gs.group_id,
        gs.student_list_id AS student_id,
        u.full_name AS student_name,
        COALESCE(SUM(h.ball), 0) AS total_score
    FROM groups_student_list gs
             JOIN users u ON gs.student_list_id = u.id
             LEFT JOIN homework h ON h.student_id = u.id AND h.checked = TRUE
    WHERE gs.group_id IN (
        SELECT group_id FROM groups_student_list WHERE student_list_id = :studentId
    )
    GROUP BY gs.group_id, gs.student_list_id, u.full_name
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
ORDER BY r.rank_position;
       
""" , nativeQuery = true)
    List<ResStudentRank> findAllByStudentRank(@Param("studentId") Long studentId);



    @Query(value = "select * from groups g join groups_students gsl on gsl.students_id = ?1", nativeQuery = true)
    Optional<Group> findByStudentId(Long studentId);

    boolean existsByName(String name);


    @Query(value = "select g.* from groups g join users u on g.teacher_id = u.id  where\n" +
            "            (:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%')))\n" +
            "            and (:teacherName IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :teacherName, '%')))\n" +
            "            and (:startDate IS NULL OR g.start_date <= :startDate)\n" +
            "            and (:endDate IS NULL OR g.end_date >= :endDate)", nativeQuery = true)
    Page<Group> searchGroup(@Param("name") String name,
                            @Param("teacherName") String teacherName,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate, Pageable pageable);


    @Query(value = "select count(*) from groups g join groups_students gs on g.id = gs.group_id join users u on gs.students_id = u.id\n" +
            "            where u.user_status = 'CHIQIB_KETGAN'", nativeQuery = true)
    Integer countGroup(Long groupId);
}
