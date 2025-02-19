package com.example.edu_platform.service;

import com.example.edu_platform.entity.Question;
import com.example.edu_platform.entity.Quiz;
import com.example.edu_platform.entity.enums.QuestionEnum;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.QuestionDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ReqQuestion;
import com.example.edu_platform.repository.QuestionRepository;
import com.example.edu_platform.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

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
        questionRepository.save(question);
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
        List<QuestionDTO> questionDTOS = questionList.stream()
                .map(this::questionDTO)
                .toList();
        return new ApiResponse(questionDTOS);
    }

    public ApiResponse deleteQuiz(Long questionId){
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null){
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }
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
        questionRepository.save(question);

        return new ApiResponse("Question yangilandi");
    }

    public QuestionDTO questionDTO(Question question){
        return QuestionDTO.builder()
                .id(question.getId())
                .text(question.getQuestion())
                .difficulty(question.getQuestionEnum().toString())
                .quizId(question.getQuiz().getId())
                .build();
    }
}
