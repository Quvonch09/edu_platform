package uz.sfera.edu_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sfera.edu_platform.entity.Income;

import java.time.Month;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income,Long> {
        @Query("SELECT i FROM Income i WHERE " +
                "(:studentName IS NULL OR UPPER(i.student.fullName) LIKE UPPER(CONCAT('%', :studentName, '%'))) AND " +
                "(:month IS NULL OR i.month = :month) AND " +
                "(:paid IS NULL OR i.paid = :paid)")
        Page<Income> search(@Param("studentName") String studentName,
                            @Param("month") Month month,
                            @Param("paid") Boolean paid,
                            Pageable pageable);

    @Query("SELECT COUNT(i) FROM Income i WHERE " +
            "(:studentName IS NULL OR UPPER(i.student.fullName) LIKE UPPER(CONCAT('%', :studentName, '%'))) AND " +
            "(:month IS NULL OR i.month = :month) AND " +
            "(:paid IS NULL OR i.paid = :paid)")
    Long countIncomes(@Param("studentName") String studentName,
                      @Param("month") Month month,
                      @Param("paid") Boolean paid);

    @Query("SELECT COALESCE(SUM(i.price), 0.0) FROM Income i WHERE " +
            "(:studentName IS NULL OR UPPER(i.student.fullName) LIKE UPPER(CONCAT('%', :studentName, '%'))) AND " +
            "(:month IS NULL OR i.month = :month) AND " +
            "(:paid IS NULL OR i.paid = :paid)")
    Double getTotalIncomePrice(@Param("studentName") String studentName,
                               @Param("month") Month month,
                               @Param("paid") Boolean paid);

    @Query("SELECT COALESCE(AVG(i.price), 0.0) FROM Income i WHERE " +
            "(:month IS NULL OR i.month = :month)")
    Double avgIncome(@Param("month") Month month);
}
