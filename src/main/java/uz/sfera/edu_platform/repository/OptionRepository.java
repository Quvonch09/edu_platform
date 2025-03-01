package uz.sfera.edu_platform.repository;

import org.springframework.data.jpa.repository.Modifying;
import uz.sfera.edu_platform.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface OptionRepository extends JpaRepository<Option,Long> {

    @Query("SELECT a FROM Option a WHERE a.question.id IN :questionIds AND a.correct = 0")
    List<Option> findCorrectAnswersByQuestionIds(@Param("questionIds") Set<Long> questionIds);

    List<Option> findByQuestionId(Long questionId);

    @Modifying
    @Query("DELETE FROM Option o WHERE o.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") Long questionId);
}
