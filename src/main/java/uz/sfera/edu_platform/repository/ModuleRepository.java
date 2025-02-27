package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module,Long> {
    boolean existsByName(String name);
    Page<Module> findByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);
    Page<Module> findByNameContainingIgnoreCaseAndDeletedFalse(String name, Pageable pageable);

    Optional<Module> findByIdAndDeletedFalse(Long id);
}
