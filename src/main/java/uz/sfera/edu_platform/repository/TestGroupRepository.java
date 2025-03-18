package uz.sfera.edu_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sfera.edu_platform.entity.TestGroup;
import uz.sfera.edu_platform.entity.User;

import java.util.List;

public interface TestGroupRepository extends JpaRepository<TestGroup,Long> {


    @Query(value = "select tg.* from test_group tg where\n" +
            "            (:name IS NULL OR LOWER(tg.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))\n" +
            "            and (:active IS NULL OR tg.active = :active)", nativeQuery = true)
    Page<TestGroup> search(@Param("name") String name, @Param("active") Boolean active, Pageable pageable);

    @Query(value = "SELECT DISTINCT tg.* FROM test_group tg " +
            "JOIN test_group_students tgs ON tgs.students_id = :studentId " +
            "LIMIT 1", nativeQuery = true)
    TestGroup findByStudentId(@Param("studentId") Long studentId);

}
