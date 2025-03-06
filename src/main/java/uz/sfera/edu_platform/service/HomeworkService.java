package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Homework;
import uz.sfera.edu_platform.entity.Task;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.exception.NotFoundException;
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

    public ApiResponse createHomework(User user, ReqHomework reqHomework) {
        return taskRepository.findById(reqHomework.getTaskId())
                .map(task -> {
                    Homework homework = Homework.builder()
                            .answer(reqHomework.getAnswer())
                            .file(fileRepository.findById(reqHomework.getFileId()).orElse(null))
                            .task(task)
                            .ball((byte) 0)
                            .checked((byte) 0)
                            .student(user)
                            .build();
                    homeworkRepository.save(homework);
                    return new ApiResponse("Homework saqlandi");
                })
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Task"))));
    }

    public ApiResponse checkHomework(Long homeworkId, byte ball) {
        return homeworkRepository.findById(homeworkId)
                .map(homework -> {
                    homework.setChecked((byte) 1);
                    homework.setBall(ball);
                    homeworkRepository.save(homework);
                    return new ApiResponse("Homework tekshirildi");
                })
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Homework"))));
    }

    public ApiResponse getMyHomeworks(boolean isChecked, User student, Long taskId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        byte checkedValue = (byte) (isChecked ? 1 : 0);

        Page<Homework> homeworks = (taskId == 0)
                ? homeworkRepository.findByCheckedAndStudentId(checkedValue, student.getId(), pageRequest)
                : homeworkRepository.findByCheckedAndTaskId(checkedValue, taskId, pageRequest);

        return homeworks.isEmpty()
                ? new ApiResponse(ResponseError.NOTFOUND("Homeworklar"))
                : new ApiResponse(new ResPageable(page, size, homeworks.getTotalPages(),
                homeworks.getTotalElements(), homeworks.map(this::homeworkDTO).toList()));
    }

    public ApiResponse getHomeworks(boolean isChecked, Long id, boolean byStudent, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        byte checkedValue = (byte) (isChecked ? 1 : 0);

        Page<Homework> homeworks = (byStudent
                ? homeworkRepository.findByCheckedAndStudentId(checkedValue, id, pageRequest)
                : homeworkRepository.findByCheckedAndTaskId(checkedValue, id, pageRequest));

        return homeworks.isEmpty()
                ? new ApiResponse(ResponseError.NOTFOUND("Homeworklar"))
                : new ApiResponse(new ResPageable(page, size, homeworks.getTotalPages(),
                homeworks.getTotalElements(), homeworks.map(this::homeworkDTO).toList()));
    }


    public ApiResponse userStatistics(User student) {
        long totalTasks = taskRepository.count();
        long completedHomeworks = homeworkRepository.countByStudentId(student.getId());

        return new ApiResponse(Map.of(
                "ballStatistics", homeworkRepository.sumBallByStudent(student) + "/" + (totalTasks * 5),
                "homeworkStatistics", completedHomeworks + "/" + totalTasks
        ));
    }

    private HomeworkDTO homeworkDTO(Homework homework){
        return HomeworkDTO.builder()
                .homeworkId(homework.getId())
                .answer(homework.getAnswer())
                .studentName(homework.getStudent().getFullName())
                .ball(homework.getBall())
                .taskId(homework.getTask().getId())
                .fileId(homework.getFile() != null ? homework.getFile().getId() : null)
                .build();
    }
}
