package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Attendance;
import uz.sfera.edu_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Attendance findByStudentAndDate(User student, LocalDate date);
    List<Attendance> getAttendanceByStudentIdAndDateBetween(Long studentId, LocalDate start, LocalDate end);
}
