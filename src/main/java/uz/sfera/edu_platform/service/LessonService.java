package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.*;
import uz.sfera.edu_platform.entity.Module;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.LessonDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.LessonRequest;
import uz.sfera.edu_platform.payload.req.ReqLessonFiles;
import uz.sfera.edu_platform.payload.req.ReqLessonTracking;
import uz.sfera.edu_platform.payload.res.ResPageable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    public final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final FileRepository fileRepository;
    private final LessonTrackingRepository lessonTrackingRepository;
    private final GroupRepository groupRepository;

    public ApiResponse createLesson(LessonRequest lessonRequest) {

        Module module = moduleRepository.findByIdAndDeletedFalse(lessonRequest.getModuleId()).orElse(null);
        if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Module"));
        }

        if (module.getCategory() == null){
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Bu modulga lesson qushish mumkin emas"));
        }

        Lesson lesson = Lesson.builder()
                .name(lessonRequest.getName())
                .description(lessonRequest.getDescription())
                .videoLink(lessonRequest.getVideoLink())
                .module(module)
                .files(null)
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
                .filter(lesson -> lesson.getModule().getCategory() != null)
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

        currentLesson.setName(lessonRequest.getName());
        currentLesson.setDescription(lessonRequest.getDescription());
        currentLesson.setVideoLink(lessonRequest.getVideoLink());
        currentLesson.setModule(module);

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
        List<Long> files = new ArrayList<>();
        for (Lesson lesson : lessons) {
            files.add(lesson.getId());
        }

        List<LessonDTO> lessonDTOS = lessons.stream()
                .filter(lesson -> lesson.getModule().getCategory() != null)
                .map(lesson -> LessonDTO.builder()
                        .lessonId(lesson.getId())
                        .name(lesson.getName())
                        .description(lesson.getDescription())
                        .videoLink(lesson.getVideoLink())
                        .fileIds(files)
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

        Group group = groupRepository.findById(groupId).orElse(null);


        List<LessonDTO> lessonDTOs = lessonTrackings.stream()
                .map(LessonTracking::getLesson)
                .filter(lesson -> !lesson.isDeleted())
                .map(this::lessonDTO)
                .toList();

        if (lessonDTOs.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Ochiq darslar"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("lessonCount", lessonDTOs.size() +"/" +lessonRepository.countLessonsByCategoryId(group != null ? group.getCategory().getId() : 0));
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

    public ApiResponse addFile(ReqLessonFiles reqLessonFiles) {
        Lesson lesson = lessonRepository.findById(reqLessonFiles.getLessonId()).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        if (reqLessonFiles.getFileIds() == null || reqLessonFiles.getFileIds().isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Fayllar"));
        }

        List<File> files = fileRepository.findAllById(reqLessonFiles.getFileIds()); // Barcha fayllarni bitta so‘rovda olish
        Set<Long> foundFileIds = files.stream().map(File::getId).collect(Collectors.toSet());
        List<Long> notFoundFiles = reqLessonFiles.getFileIds().stream()
                .filter(id -> !foundFileIds.contains(id))
                .toList();

        if (!notFoundFiles.isEmpty()) {
            return new ApiResponse("Fayllar topilmadi: " + notFoundFiles);
        }

        lesson.setFiles(files);
        return new ApiResponse("Fayllar saqlandi");
    }

    public ApiResponse deleteFiles(ReqLessonFiles reqLessonFiles) {
        Lesson lesson = lessonRepository.findById(reqLessonFiles.getLessonId()).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        if (reqLessonFiles.getFileIds() == null || reqLessonFiles.getFileIds().isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Fayllar"));
        }
        List<File> lessonFiles = lesson.getFiles();
        Set<Long> lessonFileIds = lessonFiles.stream().map(File::getId).collect(Collectors.toSet());

        List<Long> deleteFileIds = reqLessonFiles.getFileIds().stream()
                .filter(lessonFileIds::contains)
                .toList();
        if (deleteFileIds.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("O‘chirish uchun mos fayllar"));
        }
        lessonFiles.removeIf(file -> deleteFileIds.contains(file.getId()));
        lessonRepository.save(lesson);

        return new ApiResponse("Fayllar o‘chirildi");
    }

    public ApiResponse updateFiles(ReqLessonFiles reqLessonFiles, List<Long> oldFiles) {
        Lesson lesson = lessonRepository.findById(reqLessonFiles.getLessonId()).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        if (reqLessonFiles.getFileIds() == null || reqLessonFiles.getFileIds().isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Yangi fayllar"));
        }

        List<File> lessonFiles = (lesson.getFiles() != null) ? lesson.getFiles() : new ArrayList<>();
        Set<Long> lessonFileIds = lessonFiles.stream().map(File::getId).collect(Collectors.toSet());

        List<Long> filesToRemove = (oldFiles != null)
                ? oldFiles.stream().filter(lessonFileIds::contains).toList()
                : Collections.emptyList();

        List<File> newFiles = fileRepository.findAllById(reqLessonFiles.getFileIds());

        Set<Long> foundNewFileIds = newFiles.stream().map(File::getId).collect(Collectors.toSet());
        List<Long> notFoundFiles = reqLessonFiles.getFileIds().stream()
                .filter(id -> !foundNewFileIds.contains(id))
                .toList();
        lessonFiles.removeIf(file -> filesToRemove.contains(file.getId()));
        lessonFiles.addAll(newFiles);
        lesson.setFiles(lessonFiles); // Yangilangan fayllarni saqlash uchun qo‘shish shart!

        lessonRepository.save(lesson);

        return new ApiResponse("Fayllar yangilandi. O‘chirilganlar: " + filesToRemove +
                ", Qo‘shilganlar: " + foundNewFileIds +
                (notFoundFiles.isEmpty() ? "" : ", Bazada yo‘q fayllar: " + notFoundFiles));
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
