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
    List<Result> findByUserId(Long userId);
    Page<Result> findByUserId(Long userId, Pageable pageable);
    Page<Result> findByQuizIdIn(List<Long> quizIds, Pageable pageable);


    @Query("SELECT r FROM Result r WHERE r.user.id = :userId AND r.quiz.id = :quizId AND DATE(r.startTime) = CURRENT_DATE")
    Result findTodayResultByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    @Query(value = "select r.* from result r where r.user_id = :userId and r.quiz_id = :quizId and r.end_time = null", nativeQuery = true)
    Result findResult(@Param("userId") Long userId, @Param("quizId") Long quizId);


}
