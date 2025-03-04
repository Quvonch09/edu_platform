package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Result;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.ResultDTO;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.LessonTrackingRepository;
import uz.sfera.edu_platform.repository.QuizRepository;
import uz.sfera.edu_platform.repository.ResultRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final LessonTrackingRepository lessonTrackingRepository;

    public ApiResponse getUserResults(Long userId, int page, int size) {
        return userRepository.findById(userId)
                .map(user -> {
                    Page<ResultDTO> resultsPage = resultRepository.findByUserId(userId, PageRequest.of(page, size))
                            .map(this::convertToDTO);

                    return resultsPage.hasContent() ?
                            new ApiResponse(new ResPageable(page, size, resultsPage.getTotalPages(),
                                    resultsPage.getTotalElements(), resultsPage.getContent())) :
                            new ApiResponse(ResponseError.NOTFOUND("Natijalar"));
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("User")));
    }


    public ApiResponse getResultById(Long resultId) {
        return resultRepository.findById(resultId)
                .map(result -> new ApiResponse(convertToDTO(result)))
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Result")));
    }

    public ApiResponse deleteResult(Long resultId) {
        if (!resultRepository.existsById(resultId)) {
            return new ApiResponse(ResponseError.NOTFOUND("Result"));
        }
        resultRepository.deleteById(resultId);
        return new ApiResponse("Result deleted successfully");
    }


    public ResultDTO convertToDTO(Result result) {
        long timeTakenMinutes = (result.getStartTime() != null && result.getEndTime() != null)
                ? Duration.between(result.getStartTime(), result.getEndTime()).toMinutes()
                : 0; // Agar vaqtlardan biri null bo‘lsa, 0 daqiqa

        return ResultDTO.builder()
                .id(result.getId())
                .userId(result.getUser().getId())
                .quizId(result.getQuiz().getId())
                .totalQuestion(result.getTotalQuestion())
                .correctAnswers(result.getCorrectAnswers())
                .timeTaken(timeTakenMinutes)
                .startTime(result.getStartTime())
                .endTime(result.getEndTime())
                .build();
    }


}
