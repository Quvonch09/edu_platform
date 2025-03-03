package uz.sfera.edu_platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sfera.edu_platform.entity.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    Page<Result> findByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT r.* FROM result r WHERE r.user_id = :userId AND r.quiz_id = :quizId AND r.end_time IS NULL LIMIT 1 ", nativeQuery = true)
    Result findResult(@Param("userId") Long userId, @Param("quizId") Long quizId);
}