package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByLessonIdAndDeleted(Long lessonId, byte deleted);
}
