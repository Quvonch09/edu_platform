package uz.sfera.edu_platform.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.*;
import uz.sfera.edu_platform.payload.*;
import uz.sfera.edu_platform.payload.req.ReqPassTest;
import uz.sfera.edu_platform.payload.req.ReqQuiz;
import uz.sfera.edu_platform.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final ResultService resultService;

    //todo bu joyda endi @Transtactional ishlatish kerak?
    @Transactional
    public ApiResponse createQuiz(ReqQuiz reqQuiz) {
        return lessonRepository.findById(reqQuiz.getLessonId())
                .map(lesson -> {
                    if (lesson.getModule().getCategory() == null) {
                        return new ApiResponse(ResponseError.DEFAULT_ERROR("Bu lessonning categoriyasi o‘chirilgan"));
                    }

                    Quiz quiz = quizRepository.save(Quiz.builder()
                            .title(reqQuiz.getTitle())
                            .lesson(lesson)
                            .deleted((byte) 0)
                            .build());

                    QuizSettings quizSettings = QuizSettings.builder()
                            .quiz(quiz)
                            .questionCount(reqQuiz.getQuestionCount())
                            .duration(reqQuiz.getDuration())
                            .build();

                    quizSettingsRepository.save(quizSettings);

                    return new ApiResponse("Quiz yaratildi");
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Lesson")));
    }


    public ApiResponse startTest(User user, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null || quiz.getDeleted() == 1) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }

        Result oldResult = resultRepository.findResult(user.getId(), quiz.getId());

        if (oldResult != null){
                return new ApiResponse(ResponseError.DEFAULT_ERROR("Yakunlanmagan testlarni yakunlashingiz kerak"));
        }


        Result result = Result.builder()
                .startTime(LocalDateTime.now())
                .endTime(null)
                .quiz(quiz)
                .totalQuestion(getRandomQuestionsForQuiz(quiz.getId()).size())
                .correctAnswers(0)
                .user(user)
                .build();
        resultRepository.save(result);
        return new ApiResponse(getRandomQuestionsForQuiz(quizId));
    }

    public ApiResponse passTest(List<ReqPassTest> passTestList, User user, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null || quiz.getDeleted() == 1) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }


        Result result = resultRepository.findResult(user.getId(), quiz.getId());
        if (result == null) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Faol test sessiyasi topilmadi!"));
        }

        QuizSettings settings = quizSettingsRepository.findByQuizId(quizId);
        if (settings == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz settings"));
        }


        List<Long> questionIds = passTestList.stream().map(ReqPassTest::getQuestionId).collect(Collectors.toList());
        Map<Long, Long> correctAnswersMap = optionRepository.findCorrectAnswersByQuestionIds(questionIds).stream()
                .collect(Collectors.toMap(
                        option -> option.getQuestion().getId(),
                        Option::getId
                ));

        long correctCount = passTestList.stream()
                .filter(reqPassTest ->
                        correctAnswersMap.containsKey(reqPassTest.getQuestionId()) && // Savol bazada mavjud bo‘lishi kerak
                                correctAnswersMap.get(reqPassTest.getQuestionId()).equals(reqPassTest.getOptionId()) // Variant to‘g‘ri bo‘lishi kerak
                )
                .count();

        result.setEndTime(LocalDateTime.now());
        result.setCorrectAnswers(correctCount);
        Result result1 = resultRepository.save(result);
        return new ApiResponse(resultService.convertToDTO(result1));
    }

    public List<QuestionDTO> getRandomQuestionsForQuiz(Long quizId) {
        QuizSettings settings = quizSettingsRepository.findByQuizId(quizId);
        return questionRepository.findRandomQuestionsByQuizId(quizId).stream()
                .limit(settings.getQuestionCount())
                .map(question -> questionService.questionDTO(question,
                        optionRepository.findByQuestionId(question.getId()).stream()
                                .map(optionService::optionDTO)
                                .toList()))
                .toList();
    }

    public ApiResponse getQuiz(Long quizId) {
        return quizRepository.findById(quizId)
                .map(quiz -> {
                    QuizSettings settings = quizSettingsRepository.findByQuizId(quizId);
                    List<QuestionDTO> questions = questionRepository.findByQuizId(quizId).stream()
                            .map(q -> questionService.questionDTO(q, optionRepository.findByQuestionId(q.getId())
                                    .stream().map(optionService::optionDTO).toList()))
                            .toList();

                    return new ApiResponse(new QuizDTO(
                            quiz.getId(),
                            quiz.getTitle(),
                            quiz.getLesson().getId(),
                            settings != null ? settings.getQuestionCount() : 0,
                            settings != null ? settings.getDuration() : 0
                    ));
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Quiz")));
    }

    public ApiResponse getQuizByLesson(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .map(lesson -> {
                    List<QuizDTO> quizDTOS = quizRepository.findByLessonId(lessonId).stream()
                            .filter(quiz -> quiz.getLesson().getModule().getCategory() != null)
                            .map(this::quizDTO)
                            .toList();

                    return quizDTOS.isEmpty()
                            ? new ApiResponse(ResponseError.NOTFOUND("Lesson bo'yicha quizlar"))
                            : new ApiResponse(quizDTOS);
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Lesson")));
    }

    public ApiResponse deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        quiz.setDeleted((byte) 1);
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
        quiz.setDeleted((byte) 0);
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
                .questionCount(quizSettingsRepository.findByQuizId(quiz.getId()).getQuestionCount())
                .duration(quizSettingsRepository.findByQuizId(quiz.getId()).getDuration())
                .build();
    }
}
