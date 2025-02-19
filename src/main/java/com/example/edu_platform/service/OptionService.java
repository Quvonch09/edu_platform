package com.example.edu_platform.service;

import com.example.edu_platform.entity.Option;
import com.example.edu_platform.entity.Question;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.OptionDTO;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ReqOption;
import com.example.edu_platform.repository.OptionRepository;
import com.example.edu_platform.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;

    public ApiResponse saveOption(boolean isCorrect,ReqOption reqOption){
        Question question = questionRepository.findById(reqOption.getQuestionId()).orElse(null);
        if (question == null){
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }
        Option option = Option.builder()
                .name(reqOption.getText())
                .question(question)
                .correct(isCorrect)
                .build();
        optionRepository.save(option);
        return new ApiResponse("Option saqlandi");
    }

    public ApiResponse updateOption(Long optionId,boolean isCorrect,ReqOption reqOption){
        Option option = optionRepository.findById(optionId).orElse(null);
        Question question = questionRepository.findById(reqOption.getQuestionId()).orElse(null);
        if (option == null){
            return new ApiResponse(ResponseError.NOTFOUND("Option"));
        } else if (question == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }
        option.setName(reqOption.getText());
        option.setCorrect(isCorrect);
        option.setQuestion(question);
        optionRepository.save(option);

        return new ApiResponse("Option yangilandi");
    }

    public ApiResponse deleteOption(Long optionId){
        Option option = optionRepository.findById(optionId).orElse(null);
        if (option == null){
            return new ApiResponse(ResponseError.NOTFOUND("Option"));
        }
        optionRepository.delete(option);
        return new ApiResponse("Option o'chirildi");
    }

    public OptionDTO optionDTO(Option option){
        return OptionDTO.builder()
                .id(option.getId())
                .text(option.getName())
                .build();
    }
}
