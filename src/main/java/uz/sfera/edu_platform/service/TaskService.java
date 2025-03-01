package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Lesson;
import uz.sfera.edu_platform.entity.Task;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.TaskDTO;
import uz.sfera.edu_platform.payload.req.ReqTask;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.LessonRepository;
import uz.sfera.edu_platform.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final LessonRepository lessonRepository;
    private final FileRepository fileRepository;

    public ApiResponse saveTask(ReqTask reqTask){
        Lesson lesson = lessonRepository.findById(reqTask.getLessonId()).orElse(null);
        File file = fileRepository.findById(reqTask.getFileId()).orElse(null);
        if (lesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        Task task = Task.builder()
                .title(reqTask.getTitle())
                .file(file)
                .lesson(lesson)
                .deleted((byte) 0)
                .build();
        taskRepository.save(task);
        return new ApiResponse("Task saqlandi");
    }

    public ApiResponse getTask(Long taskId){
        Task foundTask = taskRepository.findById(taskId).orElse(null);
        if (foundTask == null){
            return new ApiResponse(ResponseError.NOTFOUND("Task"));
        }
        return new ApiResponse(taskDTO(foundTask));
    }

    public ApiResponse getTaskInLesson(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        
        List<Task> tasks = taskRepository.findByLessonIdAndDeleted(lessonId, (byte) 0);

        if (tasks.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Tasklar"));
        }

        List<TaskDTO> taskDTOS = tasks.stream()
                .map(this::taskDTO)
                .toList();
        return new ApiResponse(taskDTOS);
    }

    public ApiResponse updateTask(Long taskId,ReqTask reqTask){
        Task task = taskRepository.findById(taskId).orElse(null);
        Lesson lesson = lessonRepository.findById(reqTask.getLessonId()).orElse(null);
        File file = fileRepository.findById(reqTask.getFileId()).orElse(null);

        if (task == null){
            return new ApiResponse(ResponseError.NOTFOUND("task"));
        } else if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        task.setTitle(reqTask.getTitle());
        task.setLesson(lesson);
        task.setFile(file);

        taskRepository.save(task);
        return new ApiResponse("Task yangilandi");
    }

    public ApiResponse delete(Long taskId){
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null){
            return new ApiResponse(ResponseError.NOTFOUND("Task"));
        }

        task.setDeleted((byte) 1);
        taskRepository.save(task);
        return new ApiResponse("Task o'chirildi");
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
