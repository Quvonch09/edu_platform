package uz.sfera.edu_platform.repository;


import org.springframework.stereotype.Repository;
import uz.sfera.edu_platform.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

}
