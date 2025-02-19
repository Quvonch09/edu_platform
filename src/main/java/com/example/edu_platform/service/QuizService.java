package com.example.edu_platform.service;

import com.example.edu_platform.entity.*;
import com.example.edu_platform.payload.*;
import com.example.edu_platform.payload.req.ReqPassTest;
import com.example.edu_platform.payload.req.ReqQuiz;
import com.example.edu_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final LessonRepository lessonRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final OptionRepository optionRepository;
    private final ResultRepository resultRepository;
    private final QuizSettingsRepository quizSettingsRepository;
    private final OptionService optionService;
    private final QuizSessionRepository quizSessionRepository;

    public ApiResponse createQuiz(ReqQuiz reqQuiz) {
        Lesson lesson = lessonRepository.findById(reqQuiz.getLessonId()).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        Quiz quiz = Quiz.builder()
                .title(reqQuiz.getTitle())
                .lesson(lesson)
                .deleted(false)
                .build();
        quizRepository.save(quiz);

        QuizSettings settings = new QuizSettings();
        settings.setQuiz(quiz);
        settings.setQuestionCount(reqQuiz.getQuestionCount());
        settings.setDuration(reqQuiz.getDuration());
        quizSettingsRepository.save(settings);

        return new ApiResponse("Quiz yaratildi");
    }

    public ApiResponse startTest(User user,Long quizId){
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null){
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        QuizSession quizSession = QuizSession.builder()
                .user(user)
                .quiz(quiz)
                .active(true)
                .startTime(LocalDateTime.now())
                .endTime(null)
                .build();
        quizSessionRepository.save(quizSession);
        return new ApiResponse(getRandomQuestionsForQuiz(quizId));
    }

    public List<QuestionDTO> getRandomQuestionsForQuiz(Long quizId) {
        QuizSettings settings = quizSettingsRepository.findByQuizId(quizId);
        List<Question> allQuestions = questionRepository.findRandomQuestionsByQuizId(quizId);

        return allQuestions.stream()
                .limit(settings.getQuestionCount())
                .map(question -> {
                    QuestionDTO questionDTO = questionService.questionDTO(question);
                    List<OptionDTO> options = optionRepository.findByQuestionId(question.getId())
                            .stream()
                            .map(optionService::optionDTO)
                            .collect(Collectors.toList());
                    questionDTO.setOptions(options);
                    return questionDTO;
                })
                .collect(Collectors.toList());
    }

    public ApiResponse getQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        return new ApiResponse(quizDTO(quiz));
    }

    public ApiResponse passTest(List<ReqPassTest> passTestList, User user, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }

        QuizSession quizSession = quizSessionRepository.findByUserAndQuizAndActiveTrue(user, quiz);
        if (quizSession == null) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Faol test sessiyasi topilmadi!"));
        }

        quizSession.setEndTime(LocalDateTime.now());
        quizSession.setActive(false);
        quizSessionRepository.save(quizSession);

        long timeTaken = java.time.Duration.between(quizSession.getStartTime(), quizSession.getEndTime()).getSeconds();

        QuizSettings settings = quizSettingsRepository.findByQuizId(quizId);
        if (settings == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz settings"));
        }

        if (timeTaken > settings.getDuration() * 60) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Testni belgilangan vaqtdan ko'proq bajardingiz!"));
        }

        List<Long> questionIds = passTestList.stream()
                .map(ReqPassTest::getQuestionId)
                .collect(Collectors.toList());

        List<Option> correctAnswers = optionRepository.findCorrectAnswersByQuestionIds(questionIds);

        long correctCount = passTestList.stream()
                .filter(reqPassTest -> correctAnswers.stream()
                        .anyMatch(answer -> answer.getId().equals(reqPassTest.getOptionId())))
                .count();

        Result result = Result.builder()
                .quiz(quiz)
                .user(user)
                .totalQuestion(passTestList.size())
                .correctAnswers((int) correctCount)
                .timeTaken(timeTaken)
                .startTime(quizSession.getStartTime())
                .endTime(quizSession.getEndTime())
                .createdAt(LocalDateTime.now())
                .build();

        resultRepository.save(result);
        return new ApiResponse("Test muvaffaqiyatli o'tkazildi!");
    }


    public ApiResponse getQuizByLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        List<Quiz> quizList = quizRepository.findByLessonId(lessonId);
        if (quizList.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson bo'yicha quizlar"));
        }
        List<QuizDTO> quizDTOS = quizList.stream()
                .map(this::quizDTO)
                .collect(Collectors.toList());
        return new ApiResponse(quizDTOS);
    }

    public ApiResponse deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        quiz.setDeleted(true);
        quizRepository.save(quiz);
        return new ApiResponse("Quiz o'chirildi");
    }

    public ApiResponse updateQuiz(Long quizId, ReqQuiz reqQuiz) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        Lesson lesson = lessonRepository.findById(reqQuiz.getLessonId()).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        if (quiz == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }

        quiz.setTitle(reqQuiz.getTitle());
        quiz.setLesson(lesson);
        quiz.setDeleted(false);
        quizRepository.save(quiz);

        QuizSettings settings = quizSettingsRepository.findByQuizId(quizId);
        if (settings != null) {
            settings.setQuestionCount(reqQuiz.getQuestionCount());
            settings.setDuration(reqQuiz.getDuration());
            quizSettingsRepository.save(settings);
        }

        return new ApiResponse("Quiz yangilandi");
    }

    private QuizDTO quizDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .lessonId(quiz.getLesson().getId())
                .build();
    }
}
