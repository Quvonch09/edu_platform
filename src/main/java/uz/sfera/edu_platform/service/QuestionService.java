package uz.sfera.edu_platform.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.File;
import uz.sfera.edu_platform.entity.Option;
import uz.sfera.edu_platform.entity.Question;
import uz.sfera.edu_platform.entity.Quiz;
import uz.sfera.edu_platform.entity.enums.QuestionEnum;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.OptionDTO;
import uz.sfera.edu_platform.payload.QuestionDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqOption;
import uz.sfera.edu_platform.payload.req.ReqQuestion;
import uz.sfera.edu_platform.repository.FileRepository;
import uz.sfera.edu_platform.repository.OptionRepository;
import uz.sfera.edu_platform.repository.QuestionRepository;
import uz.sfera.edu_platform.repository.QuizRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final FileRepository fileRepository;
    private final OptionService optionService;
    private final OptionRepository optionRepository;

    @Transactional
    public ApiResponse saveQuestion(QuestionEnum difficulty, ReqQuestion reqQuestion) {
        Quiz quiz = quizRepository.findById(reqQuestion.getQuizId()).orElse(null);
        if (quiz == null || quiz.getDeleted() == 1) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }

        if (reqQuestion.getReqOptionList().stream().filter(ReqOption::isCorrect).count() != 1) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Har bir savol uchun faqat 1 ta to'g'ri javob bo‘lishi kerak"));
        }

        File file = reqQuestion.getFileId() != null ? fileRepository.findById(reqQuestion.getFileId()).orElse(null) : null;

        Question question = questionRepository.save(
                Question.builder()
                        .question(reqQuestion.getQuestionText())
                        .file(file)
                        .questionEnum(difficulty)
                        .quiz(quiz)
                        .build()
        );

        List<Option> options = reqQuestion.getReqOptionList().stream()
                .map(opt -> Option.builder()
                        .question(question)
                        .name(opt.getText())
                        .correct((byte) (opt.isCorrect() ? 1 : 0))
                        .build())
                .toList();

        optionRepository.saveAll(options);

        return new ApiResponse("Savol yaratildi");
    }



    public ApiResponse getQuestionByQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        if (quiz.getDeleted() == 1) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }

        List<QuestionDTO> questionDTOList = questionRepository.findByQuizId(quizId)
                .stream()
                .filter(q -> q.getQuiz().getLesson().getModule().getCategory() != null)
                .map(q -> questionDTO(q,
                        optionRepository.findByQuestionId(q.getId())
                                .stream()
                                .map(optionService::optionDTO)
                                .toList()))
                .toList();

        return questionDTOList.isEmpty()
                ? new ApiResponse(ResponseError.NOTFOUND("Questionlar"))
                : new ApiResponse(questionDTOList);
    }


    @Transactional
    public ApiResponse deleteQuiz(Long questionId) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        optionRepository.deleteByQuestionId(questionId); // Optionlarni o‘chirish
        questionRepository.delete(question); // Savolni o‘chirish

        return new ApiResponse("Question o'chirildi");
    }



    @Transactional
    public ApiResponse updateQuestion(Long questionId, QuestionEnum difficulty, ReqQuestion reqQuestion) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }

        Quiz quiz = quizRepository.findById(reqQuestion.getQuizId()).orElse(null);
        if (quiz == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }

        if (reqQuestion.getReqOptionList().stream().filter(ReqOption::isCorrect).count() != 1) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Har bir savol uchun faqat 1 ta to‘g‘ri javob bo‘lishi kerak"));
        }

        File file = null;
        if (reqQuestion.getFileId() != null){
            file = fileRepository.findById(reqQuestion.getFileId()).orElse(null);
            if (file == null){
                return new ApiResponse(ResponseError.NOTFOUND("File"));
            }
        }

        // Savolni yangilash
        question.setQuestion(reqQuestion.getQuestionText());
        question.setFile(file != null ? file : null);
        question.setQuestionEnum(difficulty);
        question.setQuiz(quiz);

        // Eski variantlarni o‘chirish
        optionRepository.deleteByQuestionId(questionId);

        // Yangi variantlarni saqlash
        List<Option> options = reqQuestion.getReqOptionList().stream()
                .map(opt -> Option.builder()
                        .question(question)
                        .name(opt.getText())
                        .correct((byte) (opt.isCorrect() ? 1 : 0))
                        .build())
                .toList();
        optionRepository.saveAll(options);

        questionRepository.save(question);
        return new ApiResponse("Question yangilandi");
    }


    public QuestionDTO questionDTO(Question question,List<OptionDTO> optionList){
        return QuestionDTO.builder()
                .id(question.getId())
                .text(question.getQuestion())
                .fileId(question.getFile() != null ? question.getFile().getId() : null)
                .difficulty(question.getQuestionEnum().toString())
                .quizId(question.getQuiz().getId())
                .options(optionList)
                .build();
    }
}
