package com.example.edu_platform.service;

import com.example.edu_platform.entity.Lesson;
import com.example.edu_platform.entity.Quiz;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.QuizDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ReqQuiz;
import com.example.edu_platform.repository.LessonRepository;
import com.example.edu_platform.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final LessonRepository lessonRepository;

    public ApiResponse createQuiz(ReqQuiz reqQuiz){
        Lesson lesson = lessonRepository.findById(reqQuiz.getLessonId()).orElse(null);
        if (lesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        Quiz quiz = Quiz.builder()
                .title(reqQuiz.getTitle())
                .lesson(lesson)
                .deleted(false)
                .build();
        quizRepository.save(quiz);
        return new ApiResponse("Quiz yaratildi");
    }

    public ApiResponse getQuiz(Long quizId){
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null){
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        return new ApiResponse(quizDTO(quiz));
    }

    public ApiResponse getQuizByLesson(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        List<Quiz> quizList = quizRepository.findByLessonId(lessonId);
        if (quizList.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson bo'yicha quizlar"));
        }
        List<QuizDTO> quizDTOS = quizList.stream()
                .map(this::quizDTO)
                .toList();
        return new ApiResponse(quizDTOS);
    }

    public ApiResponse deleteQuiz(Long quizId){
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null){
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        quiz.setDeleted(true);
        quizRepository.save(quiz);
        return new ApiResponse("Quiz o'chirildi");
    }

    public ApiResponse updateQuiz(Long quizId,ReqQuiz reqQuiz){
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        Lesson lesson = lessonRepository.findById(reqQuiz.getLessonId()).orElse(null);
        if (lesson == null){
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }else if (quiz == null){
            return new ApiResponse(ResponseError.NOTFOUND("Quiz"));
        }
        quiz.setTitle(reqQuiz.getTitle());
        quiz.setLesson(lesson);
        quiz.setDeleted(false);
        quizRepository.save(quiz);
        return new ApiResponse("Quiz yangilandi");
    }

    private QuizDTO quizDTO(Quiz quiz){
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .lessonId(quiz.getLesson().getId())
                .build();
    }
}
