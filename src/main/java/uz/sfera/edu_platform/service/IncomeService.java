package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Income;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.IncomeDTO;
import uz.sfera.edu_platform.payload.PaymentDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqIncome;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResPayment;
import uz.sfera.edu_platform.repository.IncomeRepository;
import uz.sfera.edu_platform.repository.OutcomeRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.time.Month;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final OutcomeRepository outcomeRepository;

    public ApiResponse createRipPayment(ReqIncome reqPayment) {
        User student = userRepository.findById(reqPayment.getStudentId()).orElse(null);

        if (student == null || student.isDeleted() || !student.getRole().equals(Role.ROLE_STUDENT)){
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        Income income = Income.builder()
                .student(student)
                .paymentDate(reqPayment.getPaymentDate())
                .paid(reqPayment.isPaid())
                .price(reqPayment.getPrice())
                .month(reqPayment.getPaymentDate().getMonth())
                .build();

        incomeRepository.save(income);
        return new ApiResponse("To'lov saqlandi");
    }


    public ApiResponse getIncomeCount(String studentName,Boolean paid,Month month){
        Long count = incomeRepository.countIncomes(studentName,month != null ? month.name() : null, paid);
        Double price = incomeRepository.getTotalIncomePrice(studentName,month != null ? month.name() : null,paid);

        PaymentDTO paymentDTO = PaymentDTO.builder()
                .countPayment(count)
                .totalPrice(price)
                .build();

        return new ApiResponse(paymentDTO);
    }

    public ApiResponse search(String studentName,Boolean paid,Month month,int page,int size) {
        Page<Income> incomes = incomeRepository.search(studentName,month != null ? month.name() : null,paid, PageRequest.of(page,size));

        if (incomes.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("To'lovlar"));
        }

        List<IncomeDTO> incomeDTOS = incomes.stream()
                .map(this::incomeDTO)
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(incomes.getTotalPages())
                .totalElements(incomes.getTotalElements())
                .body(incomeDTOS)
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse  getStatistic(){
        return new ApiResponse(incomeRepository.getMonthlyFinanceReport());
    }


    public ApiResponse getPaymentCount() {
        int studentCount = Optional.ofNullable(userRepository.countAllByStudent()).orElse(0);
        int countStudentsHasPaid = Optional.ofNullable(userRepository.countStudentsHasPaid()).orElse(0);
        Double tushum = Optional.ofNullable(incomeRepository.countPrice()).orElse(0.0);
        Double chiqim = Optional.ofNullable(outcomeRepository.countPrice()).orElse(0.0);

        ResPayment resPayment = ResPayment.builder()
                .countAllStudent(studentCount)
                .tulovQilganStudent(countStudentsHasPaid)
                .tulovQilmaganStudent(Math.max(studentCount - countStudentsHasPaid, 0))
                .tushum(tushum)
                .chiqim(chiqim)
                .build();

        return new ApiResponse(resPayment);
    }



    private IncomeDTO incomeDTO(Income income){
        return IncomeDTO.builder()
                .id(income.getId())
                .studentName(income.getStudent().getFullName())
                .paymentDate(income.getPaymentDate())
                .paymentMonth(income.getMonth())
                .price(income.getPrice())
                .paid(income.isPaid())
                .build();
    }
}
