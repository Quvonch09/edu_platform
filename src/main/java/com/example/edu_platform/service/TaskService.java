package com.example.edu_platform.service;

import com.example.edu_platform.entity.File;
import com.example.edu_platform.entity.Lesson;
import com.example.edu_platform.entity.Task;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.TaskDTO;
import com.example.edu_platform.payload.req.ReqTask;
import com.example.edu_platform.repository.FileRepository;
import com.example.edu_platform.repository.LessonRepository;
import com.example.edu_platform.repository.TaskRepository;
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
        List<Task> tasks = taskRepository.findByLessonId(lessonId);
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
        } else if (file == null) {
            return new ApiResponse(ResponseError.NOTFOUND("File"));
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
        taskRepository.delete(task);
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
