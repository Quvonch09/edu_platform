package com.example.edu_platform.service;

import com.example.edu_platform.entity.*;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResultDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final QuizRepository quizRepository;
    private final LessonTrackingRepository lessonTrackingRepository;

    public ApiResponse getUserResults(Long userId, int page, int size) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Result> resultsPage = resultRepository.findByUserId(userId, pageRequest);

        if (resultsPage.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Natijalar"));
        }

        List<ResultDTO> results = resultsPage.stream()
                .map(this::convertToDTO)
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(resultsPage.getTotalPages())
                .totalElements(resultsPage.getTotalElements())
                .body(results)
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse getUserResultHistory(User user, int page, int size) {
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Result> historyPage = resultRepository.findByUserId(user.getId(), pageRequest);

        if (historyPage.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Natija tarixi"));
        }

        List<ResultDTO> history = historyPage.stream()
                .map(this::convertToDTO)
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(historyPage.getTotalPages())
                .totalElements(historyPage.getTotalElements())
                .body(history)
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse getResultById(Long resultId) {
        Result result = resultRepository.findById(resultId).orElse(null);
        if (result == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Result"));
        }
        return new ApiResponse(convertToDTO(result));
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
        if (lessonIds.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Guruhga tegishli darslar"));
        }

        List<Long> quizIds = quizRepository.findQuizIdsByLessonIds(lessonIds);
        if (quizIds.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Guruhga tegishli testlar"));
        }

        Page<Result> resultsPage = resultRepository.findByQuizIdIn(quizIds, pageRequest);
        if (resultsPage.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Guruh imtihon natijalari"));
        }
        Map<String, Integer> studentTotalScores = new HashMap<>();

        resultsPage.forEach(result -> {
            String userName = result.getUser().getFullName();
            int totalScore = studentTotalScores.getOrDefault(userName, 0) + result.getCorrectAnswers();
            studentTotalScores.put(userName, totalScore);
        });
        List<Map<String, Object>> rankedResults = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<String, Integer> entry : studentTotalScores.entrySet()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .toList()) {

            Map<String, Object> studentData = new HashMap<>();
            studentData.put("rank", rank++);
            studentData.put("userName", entry.getKey());
            studentData.put("totalScore", entry.getValue());
            rankedResults.add(studentData);
        }

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
