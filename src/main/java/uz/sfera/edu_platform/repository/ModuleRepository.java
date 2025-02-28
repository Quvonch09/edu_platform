package uz.sfera.edu_platform.repository;

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
}
