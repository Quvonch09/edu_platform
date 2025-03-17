package uz.sfera.edu_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sfera.edu_platform.entity.Income;
import uz.sfera.edu_platform.payload.res.ResPaymentStatistic;

import java.time.Month;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income,Long> {
        @Query(value = "select i.* from income i join users u on u.id = i.student_id where\n" +
                "    (:studentName IS NULL OR UPPER(u.full_name) LIKE UPPER(CONCAT('%', :studentName, '%')))\n" +
                "    and (:month IS NULL OR i.month = :month)\n" +
                "    and (:paid IS NULL OR i.paid = :paid) order by i.created_at desc " ,nativeQuery = true)
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

    @Query(value = "select coalesce(sum(price), 0) from income", nativeQuery=true)
    Double countPrice();

    @Query(value = """
            WITH months AS (
                              SELECT
                                  TO_CHAR(d, 'Month') AS month_name,
                                  d AS month_start
                              FROM generate_series(
                                           DATE_TRUNC('year', CURRENT_DATE),
                                           DATE_TRUNC('month', CURRENT_DATE),
                                           INTERVAL '1 month'
                                   ) d
                          )
                          SELECT
                              m.month_name AS month,
                              COALESCE(SUM(p.price), 0) AS income,
                              COALESCE(SUM(o.price), 0) AS outcome,
                              COALESCE(SUM(p.price) - SUM(o.price), 0) AS revenue
                          FROM months m
                                   LEFT JOIN income p ON DATE_TRUNC('month', p.payment_date) = m.month_start
                                   LEFT JOIN outcome o ON DATE_TRUNC('month', o.payment_date) = m.month_start
                          WHERE m.month_start <= DATE_TRUNC('month', CURRENT_DATE) -- ❗ Faqat hozirgi oygacha
                          GROUP BY m.month_name, m.month_start
                          ORDER BY m.month_start;
    """, nativeQuery = true)
    List<ResPaymentStatistic> getMonthlyFinanceReport();
}
