package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Payment;
import uz.sfera.edu_platform.entity.enums.PaymentEnum;
import uz.sfera.edu_platform.payload.res.ResPaymentStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


    @Query( "SELECT coalesce( SUM(p.price) , 0.0)\n" +
            "FROM Payment  p \n" +
            "WHERE p.paymentType = :paymentType and extract(month from p.paymentDate) " +
            " = extract(month from current_date ) ")
    Double countPrice(@Param("paymentType") PaymentEnum paymentType);


    @Query(value = "select p.* from payment p left join users u on p.student_id = u.id where\n" +
            "            (:name IS NULL OR LOWER(p.user_name) LIKE LOWER(CONCAT('%', :name, '%')))\n" +
            "            and (:paymentStatus IS NULL OR p.payment_status = :paymentStatus)\n" +
            "            and (:paymentEnum IS NULL OR p.payment_type = :paymentEnum)\n" +
            "            and (:paid IS NULL OR p.paid = :paid)" +
            "            and (:month IS NULL OR p.month = :month)   ", nativeQuery = true)
    Page<Payment> searchPayments(@Param("name") String name,
                                 @Param("paid") Byte paid,
                                 @Param("paymentStatus") String paymentStatus,
                                 @Param("paymentEnum") String paymentEnum,
                                 @Param("month") String month,
                                 Pageable pageable);

    @Query("select  coalesce( avg(p.price) , 0.0 )  from Payment p where p.paymentType = 'TUSHUM' and extract(month from p.paymentDate) = "
        +" extract(month from current_date ) ")
    Double avgPayment();

    @Query(value = """
    WITH months AS (
        SELECT TO_CHAR(d, 'Month') AS month_name, d AS month_start
        FROM generate_series(
            DATE_TRUNC('year', CURRENT_DATE), 
            DATE_TRUNC('month', CURRENT_DATE), 
            INTERVAL '1 month'
        ) d
    )
    SELECT 
        m.month_name AS month,
        COALESCE(SUM(CASE WHEN p.payment_type = 'TUSHUM' THEN p.price ELSE 0 END), 0) AS income,
        COALESCE(SUM(CASE WHEN p.payment_type = 'CHIQIM' THEN p.price ELSE 0 END), 0) AS outcome,
        COALESCE(
            SUM(CASE WHEN p.payment_type = 'TUSHUM' THEN p.price ELSE 0 END) - 
            SUM(CASE WHEN p.payment_type = 'CHIQIM' THEN p.price ELSE 0 END), 
            0
        ) AS revenue
    FROM months m
    LEFT JOIN payment p ON DATE_TRUNC('month', p.payment_date) = m.month_start
    WHERE m.month_start <= DATE_TRUNC('month', CURRENT_DATE) -- ❗ Faqat hozirgi oygacha
    GROUP BY m.month_name, m.month_start
    ORDER BY m.month_start
    """, nativeQuery = true)
    List<ResPaymentStatistic> getMonthlyFinanceReport();


}
