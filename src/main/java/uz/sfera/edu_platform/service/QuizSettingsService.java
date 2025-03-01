package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.QuizSettings;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.QuizSettingsDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.repository.QuizRepository;
import uz.sfera.edu_platform.repository.QuizSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizSettingsService {
    private final QuizSettingsRepository quizSettingsRepository;
    private final QuizRepository quizRepository;

    public ApiResponse getSettings(Long quizId) {
        return Optional.ofNullable(quizSettingsRepository.findByQuizId(quizId))
                .map(settings -> new ApiResponse(quizSettingsDTO(settings)))
                .orElse(new ApiResponse(ResponseError.NOTFOUND("parametrlar")));
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
