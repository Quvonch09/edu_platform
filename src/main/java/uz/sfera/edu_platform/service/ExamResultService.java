package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Category;
import uz.sfera.edu_platform.entity.ExamResult;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ExamResultDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ExamResultRequest;
import uz.sfera.edu_platform.repository.ExamResultRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.time.Month;

@Service
@RequiredArgsConstructor
public class ExamResultService {
    private final ExamResultRepository examResultRepository;
    private final UserRepository userRepository;

    public ApiResponse createExamResult(Month month,ExamResultRequest examResultRequest){
        User student = userRepository.findById(examResultRequest.getStudentId()).orElse(null);
        if (student == null){
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }
        ExamResult examResult = ExamResult.builder()
                .student(student)
                .ball(examResultRequest.getBall())
                .month(month)
                .build();
        examResultRepository.save(examResult);
        return new ApiResponse("Imtihon natijasi saqlandi");
    }

    public ApiResponse getAll(Month month, Long studentId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ExamResult> pages;
        if (month == null && studentId == null) {
            pages = examResultRepository.findAll(pageRequest);
        } else if (month == null) {
            pages = examResultRepository.findByStudentId(studentId, pageRequest);
        } else if (studentId == null) {
            pages = examResultRepository.findByMonth(month, pageRequest);
        } else {
            pages = examResultRepository.findByMonthAndStudentId(month, studentId, pageRequest);
        }

        if (pages.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Imtihon natijalari topilmadi"));
        }

        Page<ExamResultDTO> resultDTOPage = pages.map(this::examResultDTO);
        return new ApiResponse(resultDTOPage);
    }

    private ExamResultDTO examResultDTO(ExamResult examResult){
        return ExamResultDTO.builder()
                .studentName(examResult.getStudent().getFullName())
                .month(examResult.getMonth().toString())
                .ball(examResult.getBall())
                .build();
    }
}
