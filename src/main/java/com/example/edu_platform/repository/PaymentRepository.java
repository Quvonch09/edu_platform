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


    @Query(value = "SELECT SUM(p.price)\n" +
            "FROM payment p\n" +
            "WHERE p.payment_type = :paymentType", nativeQuery = true)
    Double countPrice(@Param("paymentType") String paymentType);


    @Query(value = "select p.* from payment p join users u on p.student_id = u.id where " +
            "(:name IS NULL OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "and (:paymentStatus IS NULL OR p.payment_status = :paymentStatus)", nativeQuery = true)
    Page<Payment> searchPayments(@Param("name") String name,
                                 @Param("paymentStatus") String paymentStatus, Pageable pageable);
}
