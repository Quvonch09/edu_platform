package uz.sfera.edu_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.Outcome;
import uz.sfera.edu_platform.entity.enums.OutcomeStatus;

import java.time.LocalDate;
import java.time.Month;

@Repository
public interface OutcomeRepository extends JpaRepository<Outcome, Long> {


    @Query(value = """
    select o.* from outcome o
    where (:teacherName IS NULL OR LOWER(o.teacher_name) LIKE LOWER(CONCAT('%', :teacherName, '%')))
    and (:month IS NULL OR o.month = :month)
    and (:status IS NULL OR o.outcome_status = :status) order by o.created_at desc
""", nativeQuery=true)
    Page<Outcome> searchOutcome(@Param("teacherName") String teacherName,
                                @Param("month") String month,
                                @Param("status") String status, Pageable pageable);


    @Query(value = "select coalesce(sum(price), 0) from outcome", nativeQuery=true)
    Double countPrice();


    @Query(value = "select count(o.*) from outcome o" +
            "    where (:teacherName IS NULL OR LOWER(o.teacher_name) LIKE LOWER(CONCAT('%', :teacherName, '%')))\n" +
            "    and (:month IS NULL OR o.month = :month)\n" +
            "    and (:status IS NULL OR o.outcome_status = :status)"
            , nativeQuery = true)
    Long countOutcomes(@Param("teacherName") String username,
                       @Param("month") String month,
                       @Param("status") String status);


    @Query(value = "select coalesce(sum(o.price), 0) from outcome o\n" +
            "                           where (:teacherName IS NULL OR LOWER(o.teacher_name) LIKE LOWER(CONCAT('%', :teacherName, '%')))\n" +
            "                           and (:month IS NULL OR o.month = :month)\n" +
            "                           and (:status IS NULL OR o.outcome_status = :status)\n"
            , nativeQuery = true)
    Double getTotalPrice(@Param("teacherName") String username,
                         @Param("month") String month,
                         @Param("status") String status);

}
