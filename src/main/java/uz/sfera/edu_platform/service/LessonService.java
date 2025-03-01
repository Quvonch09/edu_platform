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

        Module module = moduleRepository.findByIdAndDeleted(lessonRequest.getModuleId(),(byte) 0).orElse(null);
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
                .deleted((byte) 0)
                .build();

        lessonRepository.save(lesson);
        return new ApiResponse("Dars muvaffaqiyatli yaratildi");
    }

    @Transactional
    public ApiResponse getLessonInModule(Long moduleId) {
        Module module = moduleRepository.findByIdAndDeleted(moduleId, (byte) 0).orElse(null);
        if (module == null) return new ApiResponse(ResponseError.NOTFOUND("Modul"));

        List<LessonDTO> lessons = lessonRepository.findByModuleIdAndDeleted(moduleId, (byte) 0).stream()
                .filter(l -> l.getModule().getCategory() != null)
                .map(this::lessonDTO).toList();

        return new ApiResponse(Map.of("lessonCount", lessons.size(), "lessons", lessons));
    }

    public ApiResponse updateLesson(Long lessonId, LessonRequest lessonRequest) {
        Lesson lesson = lessonRepository.findByIdAndDeleted(lessonId, (byte) 0).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        Module module = moduleRepository.findByIdAndDeleted(lessonRequest.getModuleId(), (byte) 0).orElse(null);
        if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Modul"));
        }
        lesson.setName(lessonRequest.getName());
        lesson.setDescription(lessonRequest.getDescription());
        lesson.setVideoLink(lessonRequest.getVideoLink());
        lesson.setModule(module);

        lessonRepository.save(lesson);
        return new ApiResponse("Lesson muvaffaqiyatli yangilandi");
    }


    public ApiResponse delete(Long lessonId) {
        return lessonRepository.findByIdAndDeleted(lessonId, (byte) 0).map(lesson -> {
            lesson.setDeleted((byte) 1);
            lessonRepository.save(lesson);
            return new ApiResponse("Lesson o'chirildi");
        }).orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Lesson")));
    }

    @Transactional
    public ApiResponse allowLesson(ReqLessonTracking req) {
        Lesson lesson = lessonRepository.findById(req.getLessonId()).orElse(null);
        Group group = groupRepository.findById(req.getGroupId()).orElse(null);

        if (lesson == null || lesson.getDeleted() == 1 || group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson yoki Group"));
        }

        if (lessonTrackingRepository.existsByLessonIdAndGroupId(lesson.getId(), group.getId())) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("LessonTracking"));
        }

        lessonTrackingRepository.save(new LessonTracking(lesson,group));
        return new ApiResponse("Lesson guruh uchun ochildi");
    }

    public ApiResponse search(String name, int size, int page) {
        Page<Lesson> lessons = name == null || name.trim().isEmpty()
                ? lessonRepository.findAll(PageRequest.of(page, size))
                : lessonRepository.findByNameAndDeleted(name, (byte) 0,PageRequest.of(page, size));

        return new ApiResponse(new ResPageable(page, size, lessons.getTotalPages(),
                lessons.getTotalElements(), lessons.map(this::lessonDTO).toList()));
    }

    @Transactional
    public ApiResponse getOpenLessonsInGroup(Long groupId) {
        List<LessonDTO> lessons = lessonTrackingRepository.findByGroupId(groupId).stream()
                .map(LessonTracking::getLesson)
                .filter(l -> l.getDeleted() == 0)
                .map(this::lessonDTO)
                .toList();

        return lessons.isEmpty()
                ? new ApiResponse(ResponseError.NOTFOUND("Ochiq darslar"))
                : new ApiResponse(Map.of("lessonCount", lessons.size(), "lessons", lessons));
    }

    public ApiResponse getStatistics() {
        long totalLessons = lessonRepository.countByDeleted((byte) 0);
        long deletedLessons = lessonRepository.countByDeleted((byte) 1);

        List<Map<String, Object>> moduleStatistics = moduleRepository.findAll().stream()
                .map(module -> {
                    long lessonCount = lessonRepository.countByModuleIdAndDeleted(module.getId(),(byte) 0);
                    long deletedLessonCount = lessonRepository.countByModuleIdAndDeleted(module.getId(), (byte) 1);

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

    public ApiResponse manageFiles(ReqLessonFiles req, boolean isAdding) {
        Lesson lesson = lessonRepository.findById(req.getLessonId()).orElse(null);
        if (lesson == null) return new ApiResponse(ResponseError.NOTFOUND("Lesson"));

        List<File> files = fileRepository.findAllById(req.getFileIds());
        Set<Long> foundFileIds = files.stream().map(File::getId).collect(Collectors.toSet());
        List<Long> notFoundFiles = req.getFileIds().stream()
                .filter(id -> !foundFileIds.contains(id))
                .toList();

        boolean changed = false;

        if (isAdding) {
            int initialSize = lesson.getFiles().size();
            lesson.getFiles().addAll(files);
            changed = lesson.getFiles().size() > initialSize;
        } else {
            changed = lesson.getFiles().removeIf(file -> foundFileIds.contains(file.getId()));
        }

        if (!changed) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR(isAdding ? "Fayllar qo‘shilmadi" : "Fayllar o‘chirilmadi"));
        }

        lessonRepository.save(lesson);
        return new ApiResponse(isAdding ? "Fayllar muvaffaqiyatli qo‘shildi" : "Fayllar muvaffaqiyatli o‘chirildi",
                notFoundFiles.isEmpty() ? null : ResponseError.NOTFOUND(notFoundFiles));
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
