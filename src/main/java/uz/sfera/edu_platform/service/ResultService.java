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

//    public ApiResponse getExamResults(Long groupId){
//        Group group = groupRepository.findById(groupId).orElse(null);
//        if (group == null){
//            return new ApiResponse(ResponseError.NOTFOUND("Group"));
//        }
//
//    }

    public ApiResponse getGroupResults(Long groupId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Long> lessonIds = lessonTrackingRepository.findLessonIdsByGroupId(groupId);
        if (lessonIds.isEmpty()) return new ApiResponse(ResponseError.NOTFOUND("Guruhga tegishli darslar"));

        List<Long> quizIds = quizRepository.findQuizIdsByLessonIds(lessonIds);
        if (quizIds.isEmpty()) return new ApiResponse(ResponseError.NOTFOUND("Guruhga tegishli testlar"));

        Page<Result> resultsPage = resultRepository.findByQuizIdIn(quizIds, pageRequest);
        if (resultsPage.isEmpty()) return new ApiResponse(ResponseError.NOTFOUND("Guruh imtihon natijalari"));

        Map<String, Integer> studentTotalScores = resultsPage.stream()
                .collect(Collectors.toMap(
                        result -> result.getUser().getFullName(),
                        Result::getCorrectAnswers,
                        Integer::sum
                ));

        AtomicInteger rank = new AtomicInteger(1);
        List<Map<String, Object>> rankedResults = studentTotalScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> {
                    Map<String, Object> studentData = new HashMap<>();
                    studentData.put("rank", rank.getAndIncrement());
                    studentData.put("userName", entry.getKey());
                    studentData.put("totalScore", entry.getValue());
                    return studentData;
                })
                .toList();

        return new ApiResponse(rankedResults);
    }



    private ResultDTO convertToDTO(Result result) {
        return ResultDTO.builder()
                .id(result.getId())
                .userId(result.getUser().getId())
                .quizId(result.getQuiz().getId())
                .totalQuestion(result.getTotalQuestion())
                .correctAnswers(result.getCorrectAnswers())
                .timeTaken(result.getTimeTaken())
                .startTime(result.getStartTime())
                .endTime(result.getEndTime())
                .createdAt(result.getCreatedAt())
                .build();
    }
}
