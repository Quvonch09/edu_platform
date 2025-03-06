package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Lesson;
import uz.sfera.edu_platform.entity.Task;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.TaskDTO;
import uz.sfera.edu_platform.payload.req.ReqTask;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.LessonRepository;
import uz.sfera.edu_platform.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final LessonRepository lessonRepository;
    private final FileRepository fileRepository;

    public ApiResponse saveTask(ReqTask reqTask) {
        Lesson lesson = lessonRepository.findById(reqTask.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson topilmadi"));

        File file = (reqTask.getFileId() != null) ?
                fileRepository.findById(reqTask.getFileId()).orElse(null) : null;

        Task task = Task.builder()
                .title(reqTask.getTitle())
                .file(file)
                .lesson(lesson)
                .deleted((byte) 0)
                .build();

        taskRepository.save(task);

        return new ApiResponse("Task muvaffaqiyatli saqlandi");
    }


    public ApiResponse getTask(Long taskId) {
        Task foundTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task topilmadi"));

        return new ApiResponse(taskDTO(foundTask));
    }


    public ApiResponse getTaskInLesson(Long lessonId) {
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson topilmadi"));

        List<Task> tasks = taskRepository.findByLessonIdAndDeleted(lessonId, (byte) 0);

        List<TaskDTO> taskDTOS = tasks.stream()
                .map(this::taskDTO)
                .toList();

        return new ApiResponse(taskDTOS);
    }


    public ApiResponse updateTask(Long taskId, ReqTask reqTask) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task topilmadi"));

        task.setLesson(lessonRepository.findById(reqTask.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson topilmadi")));

        if (reqTask.getFileId() != null)
            task.setFile(fileRepository.findById(reqTask.getFileId()).orElse(null));

        if (reqTask.getTitle() != null && !reqTask.getTitle().isEmpty())
            task.setTitle(reqTask.getTitle());

        taskRepository.save(task);
        return new ApiResponse("Task yangilandi");
    }


    public ApiResponse delete(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task topilmadi"));

        task.setDeleted((byte) 1);
        taskRepository.save(task);
        return new ApiResponse("Task o‘chirildi");
    }


    private TaskDTO taskDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .fileId(task.getFile() != null ? task.getFile().getId() : null)
                .lessonId(task.getLesson().getId())
                .build();
    }

}
