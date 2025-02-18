package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Integer countByTeacherId(Long teacherId);

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
