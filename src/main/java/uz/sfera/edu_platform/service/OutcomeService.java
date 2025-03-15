package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Outcome;
import uz.sfera.edu_platform.entity.enums.OutcomeStatus;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.PaymentDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqOutcome;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.repository.OutcomeRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutcomeService {
    private final OutcomeRepository outcomeRepository;

    public ApiResponse saveOutcome(ReqOutcome reqOutcome, OutcomeStatus outcomeStatus) {

        Outcome outcome = Outcome.builder()
                .price(reqOutcome.getPrice())
                .month(reqOutcome.getPaymentDate().getMonth())
                .teacherName(reqOutcome.getTeacherName())
                .paymentDate(reqOutcome.getPaymentDate())
                .outcomeStatus(outcomeStatus)
                .build();
        outcomeRepository.save(outcome);

        return new ApiResponse("Successfully saved outcome");
    }


    public ApiResponse searchOutcome(String teacherName, Month month, OutcomeStatus outcomeStatus, int page, int size) {
        Page<Outcome> outcomes = outcomeRepository.searchOutcome(
                teacherName, month != null ? month.name() : null,
                outcomeStatus != null ? outcomeStatus.name() : null, PageRequest.of(page, size)
        );

        if (outcomes.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Outcomes"));
        }

        List<ReqOutcome> reqOutcomes = new ArrayList<>();
        for (Outcome outcome : outcomes.getContent()) {
            ReqOutcome reqOutcome = ReqOutcome.builder()
                    .id(outcome.getId())
                    .price(outcome.getPrice())
                    .teacherName(outcome.getTeacherName())
                    .paymentDate(outcome.getPaymentDate())
                    .outcomeStatus(outcome.getOutcomeStatus().name())
                    .month(outcome.getMonth().name())
                    .build();
            reqOutcomes.add(reqOutcome);
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(outcomes.getTotalPages())
                .totalElements(outcomes.getTotalElements())
                .body(reqOutcomes)
                .build();
        return new ApiResponse(resPageable);
    }


    public ApiResponse getCountOutcome(String teacherName, Month month, OutcomeStatus outcomeStatus) {
        Long count = outcomeRepository.countOutcomes(teacherName,month != null ? month.name() : null, outcomeStatus != null ? outcomeStatus.name() : null);
        Double price = outcomeRepository.getTotalPrice(teacherName, month != null ? month.name() : null, outcomeStatus != null ? outcomeStatus.name() : null);

        PaymentDTO paymentDTO = PaymentDTO.builder()
                .totalPrice(price)
                .countPayment(count)
                .build();

        return new ApiResponse(paymentDTO);
    }


    public ApiResponse deleteOutcome(Long id) {
        Outcome outcome = outcomeRepository.findById(id).orElse(null);
        if (outcome == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Outcome"));
        }

        outcomeRepository.delete(outcome);
        return new ApiResponse("Successfully deleted outcome");
    }



}
