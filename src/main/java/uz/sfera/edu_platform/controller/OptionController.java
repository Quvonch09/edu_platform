package uz.sfera.edu_platform.controller;

import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.req.ReqOption;
import uz.sfera.edu_platform.service.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/option")
public class OptionController {
    private final OptionService optionService;

    @GetMapping("/getByQuestion/{questionId}")
    @Operation(summary = "Question bo'yicha optionlarni ko'rish")
    public ResponseEntity<ApiResponse> getByQuestion(
            @PathVariable Long questionId
    ){
        return ResponseEntity.ok(optionService.getByQuestion(questionId));
    }

}
