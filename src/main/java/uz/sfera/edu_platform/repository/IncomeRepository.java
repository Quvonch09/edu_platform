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
        @Query(value = "select i.* from income i join users u on u.id = i.student_id where\n" +
                "    (:studentName IS NULL OR UPPER(u.full_name) LIKE UPPER(CONCAT('%', :studentName, '%')))\n" +
                "    and (:month IS NULL OR i.month = :month)\n" +
                "    and (:paid IS NULL OR i.paid = :paid)" ,nativeQuery = true)
        Page<Income> search(@Param("studentName") String studentName,
                            @Param("month") String month,
                            @Param("paid") Boolean paid,
                            Pageable pageable);

    @Query(value = "select count(*) \n" +
            "from income i \n" +
            "join users u on u.id = i.student_id \n" +
            "where \n" +
            "    (:studentName IS NULL OR UPPER(u.full_name) LIKE UPPER(CONCAT('%', :studentName, '%')))\n" +
                          "    and (:month IS NULL OR i.month = :month)\n" +
                            "    and (:paid IS NULL OR i.paid = :paid)\n", nativeQuery = true)
    Long countIncomes(@Param("studentName") String studentName,
                      @Param("month") String month,
                      @Param("paid") Boolean paid);

    @Query(value = "select coalesce(sum(i.price), 0) \n" +
            "from income i \n" +
            "join users u on i.student_id = u.id \n" +
            "where \n" +
            "    (:studentName IS NULL OR UPPER(u.full_name) LIKE UPPER(CONCAT('%', :studentName, '%')) OR :studentName IS NULL)\n" +
            "    and (:month IS NULL OR i.month = :month OR :month IS NULL)\n" +
            "    and (:paid IS NULL OR i.paid = :paid OR :paid IS NULL)\n", nativeQuery = true)
    Double getTotalIncomePrice(@Param("studentName") String studentName,
                               @Param("month") String month,
                               @Param("paid") Boolean paid);

    @Query("SELECT COALESCE(AVG(i.price), 0.0) FROM Income i WHERE " +
            "(:month IS NULL OR i.month = :month)")
    Double avgIncome(@Param("month") Month month);
}
