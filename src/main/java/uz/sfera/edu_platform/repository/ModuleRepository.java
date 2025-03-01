package uz.sfera.edu_platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sfera.edu_platform.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module,Long> {
    boolean existsByName(String name);

    Page<Module> findByCategoryIdAndDeleted(Long categoryId, byte deleted, Pageable pageable);

    Page<Module> findByNameContainingIgnoreCaseAndDeleted(String name, byte deleted, Pageable pageable);

    Optional<Module> findByIdAndDeleted(Long id,byte deleted);

    List<Module> findAllByCategoryIdAndDeleted(Long categoryId, byte deleted);


    @Query(value = "SELECT EXISTS (SELECT 1 FROM module m\n" +
            "                        JOIN lesson l ON l.module_id = m.id\n" +
            "                        JOIN lesson_tracking lt ON lt.lesson_id = l.id\n" +
            "                        WHERE m.id = :moduleId and lt.group_id = :groupId and m.deleted = 0)",
            nativeQuery = true)
    boolean checkOpenModulesByStudent(@Param("groupId") Long groupId, @Param("moduleId") Long moduleId);

}
