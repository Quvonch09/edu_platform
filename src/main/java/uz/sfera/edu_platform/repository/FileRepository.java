package uz.sfera.edu_platform.repository;


import uz.sfera.edu_platform.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}
