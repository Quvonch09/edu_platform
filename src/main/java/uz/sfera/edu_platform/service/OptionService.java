package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.Option;
import uz.sfera.edu_platform.entity.Question;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.OptionDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqOption;
import uz.sfera.edu_platform.repository.OptionRepository;
import uz.sfera.edu_platform.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;

    public String saveOption(Long questionId, List<ReqOption> reqOptions) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    List<Option> options = reqOptions.stream()
                            .map(opt -> Option.builder()
                                    .name(opt.getText())
                                    .correct(opt.getIsCorrect())
                                    .question(question)
                                    .build())
                            .toList();
                    optionRepository.saveAll(options);
                    return "Optionlar saqlandi";
                })
                .orElse("Question not found");
    }


//    public ApiResponse updateOption(Long optionId,ReqOption reqOption){
//        Option option = optionRepository.findById(optionId).orElse(null);
//        if (option == null){
//            return new ApiResponse(ResponseError.NOTFOUND("Option"));
//        }
//        option.setName(reqOption.getText());
//        option.setCorrect(reqOption.isCorrect());
//        optionRepository.save(option);
//
//        return new ApiResponse("Option yangilandi");
//    }

    public ApiResponse getByQuestion(Long questionId){
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null){
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }
        List<Option> optionList = optionRepository.findByQuestionId(questionId);
        if (optionList.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Optionlar"));
        }
        List<OptionDTO> optionDTOS = optionList.stream()
                .map(this::optionDTO)
                .toList();
        return new ApiResponse(optionDTOS);
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
                .isCorrect(option.getCorrect())
                .build();
    }
}
