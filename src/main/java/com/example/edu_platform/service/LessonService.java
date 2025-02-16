package com.example.edu_platform.service;

import com.example.edu_platform.entity.*;
import com.example.edu_platform.entity.Module;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.LessonDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.LessonRequest;
import com.example.edu_platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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

//        File kiritmasligi mumkin, majburiy qilish shartmas

//        List<File> files = fileRepository.findAllById(lessonRequest.getFileIds());
//        if (files.size() != lessonRequest.getFileIds().size()) {
//            List<Long> notFoundFileIds = lessonRequest.getFileIds().stream()
//                    .filter(id -> files.stream().noneMatch(file -> file.getId().equals(id)))
//                    .toList();
//            return new ApiResponse(ResponseError.NOTFOUND("Fayllar: " + notFoundFileIds));
//        }

        Lesson lesson = Lesson.builder()
                .name(lessonRequest.getName())
                .description(lessonRequest.getDescription())
                .videoLink(lessonRequest.getVideoLink())
                .module(module)
                .files(findFilesByLessonId(lessonRequest.getFileIds()))
                .deleted(false)
                .build();

        lessonRepository.save(lesson);
        return new ApiResponse("Dars muvaffaqiyatli yaratildi");
    }

    @Transactional
    public ApiResponse getLessonInModule(Long moduleId) {

        Module optionalModule = moduleRepository.findById(moduleId).orElse(null);
        if (optionalModule == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }

        List<Lesson> foundLessons = lessonRepository.findByModuleId(moduleId);
        List<LessonDTO> lessonDTOs = foundLessons.stream()
                .map(this::lessonDTO)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("lessonCount", lessonDTOs.size());
        response.put("lessons", lessonDTOs);

        return new ApiResponse(response);
    }

    public ApiResponse update(Long lessonId,LessonRequest lessonRequest){
        Lesson currentLesson = lessonRepository.findById(lessonId).orElse(null);
        Module module = moduleRepository.findById(lessonRequest.getModuleId()).orElse(null);
        if (currentLesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        } else if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }

//        File kiritmasligi ham mumkin

//        List<File> files = fileRepository.findAllById(lessonRequest.getFileIds());
//        if (files.size() != lessonRequest.getFileIds().size()) {
//            List<Long> notFoundFileIds = lessonRequest.getFileIds().stream()
//                    .filter(id -> files.stream().noneMatch(file -> file.getId().equals(id)))
//                    .toList();
//            return new ApiResponse(ResponseError.NOTFOUND("Fayllar: " + notFoundFileIds));
//        }

        currentLesson.setName(lessonRequest.getName());
        currentLesson.setDescription(lessonRequest.getDescription());
        currentLesson.setVideoLink(lessonRequest.getVideoLink());
        currentLesson.setModule(module);
        currentLesson.setFiles(findFilesByLessonId(lessonRequest.getFileIds()));

        lessonRepository.save(currentLesson);
        return new ApiResponse("Lesson yangilandi");
    }

    public ApiResponse delete(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        lesson.setDeleted(true);
        lessonRepository.save(lesson);
        return new ApiResponse("Lesson o'chirildi");
    }

    public ApiResponse allowLesson(Long groupId,Long lessonId){
        boolean b = lessonTrackingRepository.existsByLessonIdAndGroupId(lessonId, groupId);
        if (b) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Bu lessonTracking"));
        }

        Group group = groupRepository.findById(groupId).orElse(null);
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        else if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        LessonTracking lessonTracking = LessonTracking.builder()
                .group(group)
                .lesson(lesson)
                .build();
        lessonTrackingRepository.save(lessonTracking);

        return new ApiResponse("Lesson guruh uchun ochildi");
    }

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
                .fileIds(lesson.getFiles() != null ? lesson.getFiles().stream().map(File::getId).toList() : null)
                .createdAt(lesson.getCreatedAt())
                .build();
    }


    private List<File> findFilesByLessonId(List<Long> lessonId) {
        List<File> fileList = new ArrayList<>();
        for (Long fileId : lessonId) {
            File file = fileRepository.findById(fileId).orElse(null);
            fileList.add(file);
        }
        return fileList;
    }
}
