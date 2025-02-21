package com.example.edu_platform.service;

import com.example.edu_platform.entity.Result;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResultDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.repository.ResultRepository;
import com.example.edu_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

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
