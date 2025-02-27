package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Homework;
import uz.sfera.edu_platform.entity.Task;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.HomeworkDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqHomework;
import uz.sfera.edu_platform.payload.res.ResPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.HomeworkRepository;
import uz.sfera.edu_platform.repository.TaskRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final TaskRepository taskRepository;
    private final FileRepository fileRepository;

    public ApiResponse createHomework(User user, ReqHomework reqHomework){
        Task task = taskRepository.findById(reqHomework.getTaskId()).orElse(null);
        File file = fileRepository.findById(reqHomework.getFileId()).orElse(null);

        if (task == null){
            return new ApiResponse(ResponseError.NOTFOUND("Task"));
        }
        Homework homework = Homework.builder()
                .answer(reqHomework.getAnswer())
                .file(file)
                .task(task)
                .ball(0)
                .checked(false)
                .student(user)
                .build();
        homeworkRepository.save(homework);
        return new ApiResponse("Homework saqlandi");
    }

    public ApiResponse checkHomework(Long homeworkId,Integer ball){
        Homework homework = homeworkRepository.findById(homeworkId).orElse(null);
        if (homework == null){
            return new ApiResponse(ResponseError.NOTFOUND("Homework"));
        }
        homework.setChecked(true);
        homework.setBall(ball);
        homeworkRepository.save(homework);
        return new ApiResponse("Homework tekshirildi");
    }

    public ApiResponse getMyHomeworks(boolean isChecked, User student, Long taskId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Homework> homeworks = (taskId == 0)
                ? homeworkRepository.findByCheckedAndStudentId(isChecked, student.getId(), pageRequest)
                : homeworkRepository.findByCheckedAndTaskId(isChecked, taskId, pageRequest);

        if (homeworks.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Homeworklar"));
        }

        List<HomeworkDTO> homeworkDTOS = homeworks.stream()
                .map(this::homeworkDTO)
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(homeworks.getTotalPages())
                .totalElements(homeworks.getTotalElements())
                .body(homeworkDTOS)
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse getHomeworks(boolean isChecked, Long id, boolean byStudent, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Homework> homeworks = byStudent
                ? homeworkRepository.findByCheckedAndStudentId(isChecked, id, pageRequest)
                : homeworkRepository.findByCheckedAndTaskId(isChecked, id, pageRequest);

        if (homeworks.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Homeworklar"));
        }

        List<HomeworkDTO> homeworkDTOS = homeworks.stream()
                .map(this::homeworkDTO)
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(homeworks.getTotalPages())
                .totalElements(homeworks.getTotalElements())
                .body(homeworkDTOS)
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse userStatistics(User student) {
        long totalTasks = taskRepository.count();
        long completedHomeworks = homeworkRepository.countByStudentId(student.getId());

        long totalPossibleBall = totalTasks * 5;
        int earnedBall = homeworkRepository.sumBallByStudent(student);

        String ballStats = earnedBall + "/" + totalPossibleBall;
        String homeworkStats = completedHomeworks + "/" + totalTasks;

        Map<String, String> statistics = Map.of(
                "ballStatistics", ballStats,
                "homeworkStatistics", homeworkStats
        );

        return new ApiResponse(statistics);
    }


    private HomeworkDTO homeworkDTO(Homework homework){
        return HomeworkDTO.builder()
                .homeworkId(homework.getId())
                .answer(homework.getAnswer())
                .studentName(homework.getStudent().getFullName())
                .ball(homework.getBall())
                .taskId(homework.getTask().getId())
                .build();
    }
}
