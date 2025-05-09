package uz.sfera.edu_platform.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndActive(String name, byte active);

    @Query("select coalesce(count (c) , 0)  from Category c where c.active = 1 ")
    Integer countAllByCategory();

    @Query(value = "select * from category c where (?1 IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%')))\n" +
            "            and (?2 IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', ?2, '%'))) and c.active = 1 order by c.created_at desc", nativeQuery = true)
    Page<Category> getAllCategory(String name, String description, Pageable pageable);

        @Query("SELECT c FROM Category c " +
                "WHERE c.active = :active " +
                "AND (:userId IS NULL OR c IN (SELECT cat FROM User u JOIN u.categories cat WHERE u.id = :userId))")
        List<Category> findAllByActiveAndTeacherId(@Param("active") byte active, @Param("userId") Long userId);

}
