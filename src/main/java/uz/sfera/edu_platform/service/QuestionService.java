package uz.sfera.edu_platform.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Option;
import uz.sfera.edu_platform.entity.Question;
import uz.sfera.edu_platform.entity.Quiz;
import uz.sfera.edu_platform.entity.enums.QuestionEnum;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.OptionDTO;
import uz.sfera.edu_platform.payload.QuestionDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqOption;
import uz.sfera.edu_platform.payload.req.ReqQuestion;
import uz.sfera.edu_platform.repository.OptionRepository;
import uz.sfera.edu_platform.repository.QuestionRepository;
import uz.sfera.edu_platform.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final OptionService optionService;
    private final OptionRepository optionRepository;

    @Transactional
    public ApiResponse saveQuestion(QuestionEnum difficulty, ReqQuestion reqQuestion) {
        Quiz quiz = quizRepository.findById(reqQuestion.getQuizId()).orElse(null);
        if (quiz == null) return new ApiResponse(ResponseError.NOTFOUND("Quiz"));

        long correctAnswers = reqQuestion.getReqOptionList().stream()
                .filter(option -> option.getIsCorrect() == 1)
                .count();

        if (correctAnswers != 1) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Har bir savol uchun faqat 1 ta to'g'ri javob bo‘lishi kerak"));
        }

        Question question = Question.builder()
                .question(reqQuestion.getQuestionText())
                .questionEnum(difficulty)
                .quiz(quiz)
                .build();
        questionRepository.save(question);
        optionService.saveOption(question.getId(), reqQuestion.getReqOptionList());

        return new ApiResponse("Savol yaratildi");
    }


    public ApiResponse getQuestionByQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) return new ApiResponse(ResponseError.NOTFOUND("Quiz"));

        List<QuestionDTO> questionDTOList = questionRepository.findByQuizId(quizId).stream()
                .filter(q -> q.getQuiz().getLesson().getModule().getCategory() != null)
                .map(q -> questionDTO(q, optionRepository.findByQuestionId(q.getId())
                        .stream().map(optionService::optionDTO).toList()))
                .toList();

        return questionDTOList.isEmpty()
                ? new ApiResponse(ResponseError.NOTFOUND("Questionlar"))
                : new ApiResponse(questionDTOList);
    }

    public ApiResponse deleteQuiz(Long questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    optionRepository.deleteByQuestionId(questionId); // Barcha optionlarni 1 ta so‘rov bilan o‘chirish
                    questionRepository.delete(question);
                    return new ApiResponse("Question o'chirildi");
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Question")));
    }


    public ApiResponse updateQuestion(Long questionId, QuestionEnum difficulty, ReqQuestion reqQuestion) {
        return questionRepository.findById(questionId)
                .map(question -> quizRepository.findById(reqQuestion.getQuizId())
                        .map(quiz -> {
                            question.setQuestion(reqQuestion.getQuestionText());
                            question.setQuestionEnum(difficulty);
                            question.setQuiz(quiz);

                            optionRepository.deleteByQuestionId(questionId);

                            int correctCount = (int) reqQuestion.getReqOptionList().stream()
                                    .filter(option -> option.getIsCorrect() == (byte) 1) // byte → boolean tekshiruvi
                                    .count();

                            if (correctCount != 1) {
                                return new ApiResponse(ResponseError.DEFAULT_ERROR("Har bir savol uchun faqat 1 ta to‘g‘ri javob bo‘lishi kerak"));
                            }

                            optionService.saveOption(question.getId(), reqQuestion.getReqOptionList());
                            questionRepository.save(question);

                            return new ApiResponse("Question yangilandi");
                        })
                        .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Quiz"))))
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Question")));
    }


    public QuestionDTO questionDTO(Question question,List<OptionDTO> optionList){
        return QuestionDTO.builder()
                .id(question.getId())
                .text(question.getQuestion())
                .difficulty(question.getQuestionEnum().toString())
                .quizId(question.getQuiz().getId())
                .options(optionList)
                .build();
    }
}
