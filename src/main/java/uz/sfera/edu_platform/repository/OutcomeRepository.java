package uz.sfera.edu_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.Outcome;

@Repository
public interface OutcomeRepository extends JpaRepository<Outcome, Long> {


    @Query(value = """
    select o.* from outcome o
    where (:teacherName IS NULL OR LOWER(o.teacher_name) LIKE LOWER(CONCAT('%', :teacherName, '%')))
    and (:month IS NULL OR o.month = :month)
    and (:status IS NULL OR o.outcome_status = :status)
""", nativeQuery=true)
    Page<Outcome> searchOutcome(@Param("teacherName") String teacherName,
                                @Param("month") String month,
                                @Param("status") String status, Pageable pageable);
}
