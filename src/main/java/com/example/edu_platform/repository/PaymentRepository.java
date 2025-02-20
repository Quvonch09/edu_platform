package com.example.edu_platform.repository;

import com.example.edu_platform.entity.Payment;
import com.example.edu_platform.entity.enums.PaymentEnum;
import com.example.edu_platform.payload.res.ResPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


    @Query( "SELECT coalesce( SUM(p.price) , 0.0)\n" +
            "FROM Payment  p \n" +
            "WHERE p.paymentType = :paymentType and extract(month from p.paymentDate) " +
            " = extract(month from current_date ) ")
    Double countPrice(@Param("paymentType") PaymentEnum paymentType);


    @Query(value = "select p.* from payment p join users u on p.student_id = u.id where " +
            "(:name IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "and (:paymentStatus IS NULL OR p.payment_status = :paymentStatus)", nativeQuery = true)
    Page<Payment> searchPayments(@Param("name") String name,
                                 @Param("paymentStatus") String paymentStatus, Pageable pageable);

    @Query("select  coalesce( avg(p.price) , 0.0 )  from Payment p where p.paymentType = 'TUSHUM' and extract(month from p.paymentDate) = "
        +" extract(month from current_date ) ")
    Double avgPayment();

}
