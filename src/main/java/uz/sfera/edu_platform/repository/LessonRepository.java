package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson,Long> {
    List<Lesson> findByModuleIdAndDeleted(Long moduleId, byte deleted);
    long countByDeleted(byte deleted);
    long countByModuleIdAndDeleted(Long moduleId, byte deleted);
    Page<Lesson> findByNameAndDeleted(String name, byte deleted, Pageable pageable);
    Optional<Lesson> findByIdAndDeleted(Long id, byte deleted);


    @Query(value = "select coalesce(count(l.*) , 0) from lesson l join module m on l.module_id = m.id " +
            " where m.category_id = :categoryId ", nativeQuery = true)
    Integer countLessonsByCategoryId(Long categoryId);

}
