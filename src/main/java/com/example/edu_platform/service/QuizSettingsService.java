package com.example.edu_platform.service;

import com.example.edu_platform.entity.Quiz;
import com.example.edu_platform.entity.QuizSettings;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.QuizSettingsDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ReqQuizSettings;
import com.example.edu_platform.repository.QuizRepository;
import com.example.edu_platform.repository.QuizSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizSettingsService {
    private final QuizSettingsRepository quizSettingsRepository;
    private final QuizRepository quizRepository;

    public ApiResponse updateSettings(Long settingId,ReqQuizSettings reqQuizSettings){
        Quiz quiz = quizRepository.findById(reqQuizSettings.getQuizId()).orElse(null);
        QuizSettings quizSettings = quizSettingsRepository.findById(settingId).orElse(null);
        if (quiz == null){
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        } else if (quizSettings == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Parametrlar"));
        }
        quizSettings.setQuiz(quiz);
        quizSettings.setDuration(reqQuizSettings.getDuration());
        quizSettings.setQuestionCount(reqQuizSettings.getQuestionCount());
        quizSettingsRepository.save(quizSettings);

        return new ApiResponse("Parametrlar yangilandi");
    }

    public ApiResponse getSettings(Long quizId){
        QuizSettings quizSettings = quizSettingsRepository.findByQuizId(quizId);
        if (quizSettings == null){
            return new ApiResponse(ResponseError.NOTFOUND("parametrlar"));
        }
        return new ApiResponse(quizSettingsDTO(quizSettings));
    }

    private QuizSettingsDTO quizSettingsDTO(QuizSettings quizSettings){
        return QuizSettingsDTO.builder()
                .id(quizSettings.getId())
                .questionCount(quizSettings.getQuestionCount())
                .duration(quizSettings.getDuration())
                .quizId(quizSettings.getQuiz().getId())
                .build();
    }
}
