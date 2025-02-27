package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    @Query("select coalesce(count (c) , 0)  from Category c where c.active is true ")
    Integer countAllByCategory();
    @Query(value = "select * from category c where (?1 IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "and (?2 IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', ?2, '%')))", nativeQuery = true)
    Page<Category> getAllCategory(String name, String description, Pageable pageable);
}
