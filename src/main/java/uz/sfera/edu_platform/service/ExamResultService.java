package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.ExamResult;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ExamResultDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ExamResultRequest;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.ExamResultRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamResultService {
    private final ExamResultRepository examResultRepository;
    private final UserRepository userRepository;

    public ApiResponse createExamResult(Month month,ExamResultRequest examResultRequest){
        User student = userRepository.findById(examResultRequest.getStudentId()).orElse(null);

        if (student == null) return new ApiResponse(ResponseError.NOTFOUND("Student"));

        ExamResult examResult = ExamResult.builder()
                .student(student)
                .ball(examResultRequest.getBall())
                .month(month)
                .build();
        examResultRepository.save(examResult);
        return new ApiResponse("Imtihon natijasi saqlandi");
    }

    public ApiResponse getAll(User teacher, Month month, Long studentId, int page, int size) {
        Page<ExamResult> pages = examResultRepository.searchResult
                (teacher.getId(),month != null ? month.name() : LocalDate.now().getMonth().name(), studentId, PageRequest.of(page, size));

        if (pages.isEmpty()) return new ApiResponse(ResponseError.NOTFOUND("Imtihon natijalari topilmadi"));

        List<ExamResultDTO> resultDTOPage = pages.map(this::examResultDTO).toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(pages.getTotalPages())
                .totalElements(pages.getTotalElements())
                .body(resultDTOPage)
                .build();

        return new ApiResponse(resPageable);
    }

    private ExamResultDTO examResultDTO(ExamResult examResult){
        return ExamResultDTO.builder()
                .studentName(examResult.getStudent().getFullName())
                .month(examResult.getMonth().toString())
                .ball(examResult.getBall())
                .build();
    }
}
