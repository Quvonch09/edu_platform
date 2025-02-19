package com.example.edu_platform.service;

import com.example.edu_platform.entity.*;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.HomeworkDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ReqHomework;
import com.example.edu_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public ApiResponse getMyHomeworks(boolean isChecked, User student, Long taskId) {
        List<Homework> homeworks = (taskId == 0)
                ? homeworkRepository.findByCheckedAndStudentId(isChecked, student.getId())
                : homeworkRepository.findByCheckedAndTaskId(isChecked, taskId);
        if (homeworks.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Homeworklar"));
        }
        List<HomeworkDTO> homeworkDTOS = homeworks.stream()
                .map(this::homeworkDTO)
                .toList();

        return new ApiResponse(homeworkDTOS);
    }

    public ApiResponse getHomeworks(boolean isChecked, Long id, boolean byStudent) {
        List<Homework> homeworks = byStudent
                ? homeworkRepository.findByCheckedAndStudentId(isChecked, id)
                : homeworkRepository.findByCheckedAndTaskId(isChecked, id);

        if (homeworks.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Homeworklar"));
        }

        List<HomeworkDTO> homeworkDTOS = homeworks.stream()
                .map(this::homeworkDTO)
                .toList();

        return new ApiResponse(homeworkDTOS);
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
