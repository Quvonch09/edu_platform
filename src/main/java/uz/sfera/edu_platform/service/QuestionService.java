package uz.sfera.edu_platform.service;

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

    public ApiResponse saveQuestion(QuestionEnum difficulty,ReqQuestion reqQuestion){
        Quiz quiz = quizRepository.findById(reqQuestion.getQuizId()).orElse(null);
        if (quiz == null){
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }

        Question question = Question.builder()
                .question(reqQuestion.getQuestionText())
                .quiz(quiz)
                .questionEnum(difficulty)
                .build();
        Question save = questionRepository.save(question);

        String apiResponse = optionService.saveOption(save.getId(), reqQuestion.getReqOptionList());
        if (!apiResponse.equals("Optionlar saqlandi")){
            return new ApiResponse(apiResponse);
        }

        return new ApiResponse("Question yaratildi");
    }

    public ApiResponse getQuestionByQuiz(Long quizId){
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null){
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        List<Question> questionList = questionRepository.findByQuizId(quizId);
        if (questionList.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Questionlar"));
        }

        List<QuestionDTO> questionDTOList = questionList.stream()
                .filter(question -> question.getQuiz().getLesson().getModule().getCategory() != null) // Filter qismi
                .map(question -> {
                    List<OptionDTO> optionDTOList = optionRepository.findByQuestionId(question.getId()).stream()
                            .map(optionService::optionDTO)
                            .toList();

                    return questionDTO(question, optionDTOList);
                })
                .toList();

        return new ApiResponse(questionDTOList);
    }

    public ApiResponse deleteQuiz(Long questionId){
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null){
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }

        optionRepository.deleteAll(optionRepository.findByQuestionId(question.getId()));
        questionRepository.delete(question);

        return new ApiResponse("Question o'chirildi");
    }

    public ApiResponse updateQuestion(Long questionId,QuestionEnum difficulty,ReqQuestion reqQuestion){
        Quiz quiz = quizRepository.findById(reqQuestion.getQuizId()).orElse(null);
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null){
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        } else if (quiz == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        question.setQuestion(reqQuestion.getQuestionText());
        question.setQuestionEnum(difficulty);
        question.setQuiz(quiz);

        optionRepository.deleteAll(optionRepository.findByQuestionId(questionId));
        optionService.saveOption(question.getId(), reqQuestion.getReqOptionList());
        questionRepository.save(question);

        return new ApiResponse("Question yangilandi");
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
