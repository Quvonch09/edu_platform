package uz.sfera.edu_platform.service;

import uz.sfera.edu_platform.entity.Payment;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.PaymentEnum;
import uz.sfera.edu_platform.entity.enums.PaymentStatusEnum;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.PaymentDTO;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResPayment;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqPayment;
import uz.sfera.edu_platform.repository.PaymentRepository;
import uz.sfera.edu_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;


    public ApiResponse createRipPayment(PaymentStatusEnum paymentStatus,
                                        PaymentEnum paymentType,
                                        ReqPayment reqPayment){
        User user = userRepository.findById(reqPayment.getUserId()).orElse(null);
        if(user == null){
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        if (Objects.equals(user.getRole(), Role.ROLE_STUDENT) && Objects.equals(paymentType, PaymentEnum.CHIQIM)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Student faqat tulov qila oladi"));
        }

        if (Objects.equals(paymentType, PaymentEnum.CHIQIM) && paymentStatus == null) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Status null bulishi mumkin emas"));
        }

        Payment payment = Payment.builder()
                .student(user)
                .price(reqPayment.getPrice())
                .paymentDate(reqPayment.getPaymentDate())
                .paymentStatus(paymentStatus)
                .paymentType(paymentType)
                .build();

        paymentRepository.save(payment);
        return new ApiResponse("Successfully saved");
    }



    public ApiResponse getPaymentCount() {
        int studentCount = Optional.ofNullable(userRepository.countAllByStudent()).orElse(0);
        int countStudentsHasPaid = Optional.ofNullable(userRepository.countStudentsHasPaid()).orElse(0);

        ResPayment resPayment = ResPayment.builder()
                .countAllStudent(studentCount)
                .tulovQilganStudent(countStudentsHasPaid)
                .tulovQilmaganStudent(Math.max(studentCount - countStudentsHasPaid, 0))
                .tushum(paymentRepository.countPrice(PaymentEnum.TUSHUM))
                .chiqim(paymentRepository.countPrice(PaymentEnum.CHIQIM))
                .build();

        return new ApiResponse(resPayment);
    }




    public ApiResponse search(String userName, PaymentStatusEnum paymentStatus, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Payment> payments = paymentRepository.searchPayments(
                userName, Optional.ofNullable(paymentStatus).map(Enum::name).orElse(null), pageRequest
        );

        List<PaymentDTO> paymentDTOList = payments.stream()
                .map(payment -> PaymentDTO.builder()
                        .id(payment.getId())
                        .fullName(payment.getStudent().getFullName())
                        .paymentDate(payment.getPaymentDate())
                        .paymentStatus(payment.getPaymentStatus())
                        .price(payment.getPrice())
                        .build())
                .toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalElements(payments.getTotalElements())
                .totalPage(payments.getTotalPages())
                .body(paymentDTOList)
                .build();

        return new ApiResponse(resPageable);
    }


    public ApiResponse updatePayment(Long paymentId,PaymentStatusEnum paymentStatus,
                                     PaymentEnum paymentType,
                                     ReqPayment reqPayment ){
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if(payment == null){
            return new ApiResponse(ResponseError.NOTFOUND("Payment"));
        }
        User user = userRepository.findById(reqPayment.getUserId()).orElse(null);
        if(user == null){
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        payment.setPaymentStatus(Objects.requireNonNull(paymentStatus, "Payment status cannot be null"));
        payment.setPaymentType(Objects.requireNonNull(paymentType, "Payment type cannot be null"));
        payment.setPaymentDate(reqPayment.getPaymentDate());
        payment.setStudent(user);
        payment.setPrice(reqPayment.getPrice());

        paymentRepository.save(payment);
        return new ApiResponse("Successfully updated");
    }




    public ApiResponse deletePayment(Long paymentId){
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if(payment == null){
            return new ApiResponse(ResponseError.NOTFOUND("Payment"));
        }

        paymentRepository.delete(payment);
        return new ApiResponse("Successfully deleted");
    }



    public ApiResponse  getStatistic(){
        return new ApiResponse(paymentRepository.getMonthlyFinanceReport());
    }
}
