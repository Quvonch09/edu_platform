package com.example.edu_platform.service;

import com.example.edu_platform.entity.*;
import com.example.edu_platform.entity.Module;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.LessonDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.LessonRequest;
import com.example.edu_platform.payload.req.ReqLessonTracking;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LessonService {
    public final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final FileRepository fileRepository;
    private final LessonTrackingRepository lessonTrackingRepository;
    private final GroupRepository groupRepository;

    public ApiResponse createLesson(LessonRequest lessonRequest) {

        Module module = moduleRepository.findById(lessonRequest.getModuleId()).orElse(null);
        if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Module"));
        }

        List<File> files = fileRepository.findAllById(lessonRequest.getFileIds());
        if (files.size() != lessonRequest.getFileIds().size()) {
            List<Long> notFoundFileIds = lessonRequest.getFileIds().stream()
                    .filter(id -> files.stream().noneMatch(file -> file.getId().equals(id)))
                    .toList();
            return new ApiResponse(ResponseError.NOTFOUND("Fayllar: " + notFoundFileIds));
        }

        Lesson lesson = Lesson.builder()
                .name(lessonRequest.getName())
                .description(lessonRequest.getDescription())
                .videoLink(lessonRequest.getVideoLink())
                .module(module)
                .files(files)
                .deleted(false)
                .build();

        lessonRepository.save(lesson);
        return new ApiResponse("Dars muvaffaqiyatli yaratildi");
    }

    @Transactional
    public ApiResponse getLessonInModule(Long moduleId) {

        Module optionalModule = moduleRepository.findByIdAndDeletedFalse(moduleId).orElse(null);
        if (optionalModule == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }

        List<Lesson> foundLessons = lessonRepository.findByModuleIdAndDeletedFalse(moduleId);
        List<LessonDTO> lessonDTOs = foundLessons.stream()
                .map(this::lessonDTO)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("lessonCount", lessonDTOs.size());
        response.put("lessons", lessonDTOs);

        return new ApiResponse(response);
    }

    public ApiResponse update(Long lessonId,LessonRequest lessonRequest){
        Lesson currentLesson = lessonRepository.findByIdAndDeletedFalse(lessonId).orElse(null);
        Module module = moduleRepository.findByIdAndDeletedFalse(lessonRequest.getModuleId()).orElse(null);
        if (currentLesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        } else if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }
        List<File> files = fileRepository.findAllById(lessonRequest.getFileIds());
        if (files.size() != lessonRequest.getFileIds().size()) {
            List<Long> notFoundFileIds = lessonRequest.getFileIds().stream()
                    .filter(id -> files.stream().noneMatch(file -> file.getId().equals(id)))
                    .toList();
            return new ApiResponse(ResponseError.NOTFOUND("Fayllar: " + notFoundFileIds));
        }
        currentLesson.setName(lessonRequest.getName());
        currentLesson.setDescription(lessonRequest.getDescription());
        currentLesson.setVideoLink(lessonRequest.getVideoLink());
        currentLesson.setModule(module);
        currentLesson.setFiles(files);

        lessonRepository.save(currentLesson);
        return new ApiResponse("Lesson yangilandi");
    }

    public ApiResponse delete(Long lessonId){
        Lesson lesson = lessonRepository.findByIdAndDeletedFalse(lessonId).orElse(null);
        if (lesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        lesson.setDeleted(true);
        lessonRepository.save(lesson);
        return new ApiResponse("Lesson o'chirildi");
    }

    @Transactional
    public ApiResponse allowLesson(ReqLessonTracking reqLessonTracking){
        Lesson lesson = lessonRepository.findById(reqLessonTracking.getLessonId()).orElse(null);
        Group group = groupRepository.findById(reqLessonTracking.getGroupId()).orElse(null);
        if (lesson == null || lesson.isDeleted()){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        } else if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }
        LessonTracking lessonTracking = LessonTracking.builder()
                .group(group)
                .lesson(lesson)
                .build();
        lessonTrackingRepository.save(lessonTracking);
        return new ApiResponse("Lesson guruh uchun ochildi");
    }

    public ApiResponse search(String name, int size, int page) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Lesson> lessons;

        if (name == null || name.trim().isEmpty()) {
            lessons = lessonRepository.findAll(pageRequest);
        } else {
            lessons = lessonRepository.findByNameAndDeletedFalse(name, pageRequest);
        }

        List<LessonDTO> lessonDTOS = lessons.stream()
                .map(lesson -> LessonDTO.builder()
                        .lessonId(lesson.getId())
                        .name(lesson.getName())
                        .description(lesson.getDescription())
                        .videoLink(lesson.getVideoLink())
                        .createdAt(lesson.getCreatedAt())
                        .build())
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(lessons.getTotalPages())
                .totalElements(lessons.getTotalElements())
                .body(lessonDTOS)
                .build();

        return new ApiResponse(resPageable);
    }


    @Transactional
    public ApiResponse getOpenLessonsInGroup(Long groupId) {
        List<LessonTracking> lessonTrackings = lessonTrackingRepository.findByGroupId(groupId);
        if (lessonTrackings.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Ochiq darslar"));
        }

        List<LessonDTO> lessonDTOs = lessonTrackings.stream()
                .map(LessonTracking::getLesson)
                .filter(lesson -> !lesson.isDeleted())
                .map(this::lessonDTO)
                .toList();

        if (lessonDTOs.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Ochiq darslar"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("lessonCount", lessonDTOs.size());
        response.put("lessons", lessonDTOs);

        return new ApiResponse(response);
    }

    public ApiResponse getStatistics() {
        long totalLessons = lessonRepository.countByDeletedFalse();
        long deletedLessons = lessonRepository.countByDeletedTrue();

        List<Map<String, Object>> moduleStatistics = moduleRepository.findAll().stream()
                .map(module -> {
                    long lessonCount = lessonRepository.countByModuleIdAndDeletedFalse(module.getId());
                    long deletedLessonCount = lessonRepository.countByModuleIdAndDeletedTrue(module.getId());

                    Map<String, Object> moduleStat = new HashMap<>();
                    moduleStat.put("moduleId", module.getId());
                    moduleStat.put("moduleName", module.getName());
                    moduleStat.put("lessonCount", lessonCount);
                    moduleStat.put("deletedLessonCount", deletedLessonCount);
                    return moduleStat;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("totalLessons", totalLessons);
        response.put("deletedLessons", deletedLessons);
        response.put("moduleStatistics", moduleStatistics);

        return new ApiResponse(response);
    }

    private LessonDTO lessonDTO(Lesson lesson) {
        return LessonDTO.builder()
                .lessonId(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .videoLink(lesson.getVideoLink())
                .fileIds(lesson.getFiles().stream().map(File::getId).toList())
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}
