package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.Result;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResultDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.res.ResPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.repository.*;

import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
                    Page<Result> resultsPage = resultRepository.findByUserId(userId, PageRequest.of(page, size));
                    return resultsPage.isEmpty() ?
                            new ApiResponse(ResponseError.NOTFOUND("Natijalar")) :
                            new ApiResponse(new ResPageable(page, size, resultsPage.getTotalPages(),
                                    resultsPage.getTotalElements(), resultsPage.map(this::convertToDTO).toList()));
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("User")));
    }

    public ApiResponse getResultById(Long resultId) {
        return resultRepository.findById(resultId)
                .map(result -> new ApiResponse(convertToDTO(result)))
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Result")));
    }

    public ApiResponse deleteResult(Long resultId) {
        Result result = resultRepository.findById(resultId).orElse(null);
        if (result == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Result"));
        }
        resultRepository.delete(result);
        return new ApiResponse("Result deleted successfully");
    }

    public ResultDTO convertToDTO(Result result) {
        if (result.getStartTime() == null || result.getEndTime() == null) {
            return ResultDTO.builder()
                    .id(result.getId())
                    .userId(result.getUser().getId())
                    .quizId(result.getQuiz().getId())
                    .totalQuestion(result.getTotalQuestion())
                    .correctAnswers(result.getCorrectAnswers())
                    .timeTaken(0) // Agar vaqt null bo‘lsa, 0 daqiqa
                    .startTime(result.getStartTime())
                    .endTime(result.getEndTime())
                    .build();
        }

        long timeTakenMinutes = Duration.between(result.getStartTime(), result.getEndTime()).toMinutes();

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
