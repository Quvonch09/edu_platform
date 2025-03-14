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
    and (:status IS NULL OR o.outcome_status = :status)
""", nativeQuery=true)
    Page<Outcome> searchOutcome(@Param("teacherName") String teacherName,
                                @Param("month") String month,
                                @Param("status") String status, Pageable pageable);


    @Query(value = "select sum(price) from outcome", nativeQuery=true)
    Double countPrice();


    @Query("SELECT COUNT(o) FROM Outcome o WHERE " +
            "(:username IS NULL OR UPPER(o.teacherName) LIKE UPPER(CONCAT('%', :username, '%'))) AND " +
            "(:month IS NULL OR o.month = :month) AND " +
            "(:status IS NULL OR o.outcomeStatus = :status) AND " +
            "(:date IS NULL OR o.paymentDate = :date)")
    Long countOutcomes(@Param("username") String username,
                       @Param("month") Month month,
                       @Param("status") OutcomeStatus status,
                       @Param("date") LocalDate date);


    @Query("SELECT COALESCE(SUM(o.price), 0.0) FROM Outcome o WHERE " +
            "(:username IS NULL OR UPPER(o.teacherName) LIKE UPPER(CONCAT('%', :username, '%'))) AND " +
            "(:month IS NULL OR o.month = :month) AND " +
            "(:status IS NULL OR o.outcomeStatus = :status) AND " +
            "(:date IS NULL OR o.paymentDate = :date)")
    Double getTotalPrice(@Param("username") String username,
                         @Param("month") Month month,
                         @Param("status") OutcomeStatus status,
                         @Param("date") LocalDate date);

}
