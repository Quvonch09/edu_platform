package com.example.edu_platform.service;

import com.example.edu_platform.entity.Result;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResultDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.repository.ResultRepository;
import com.example.edu_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

    public ApiResponse getUserResults(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }
        List<ResultDTO> results = resultRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ApiResponse(results);
    }

    public ApiResponse getUserResultHistory(User user) {
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }
        List<ResultDTO> history = resultRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ApiResponse(history);
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
