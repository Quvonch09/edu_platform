package uz.sfera.edu_platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.sfera.edu_platform.entity.Payment;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.PaymentEnum;
import uz.sfera.edu_platform.entity.enums.PaymentStatusEnum;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.exception.NotFoundException;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.PaymentDTO;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.req.ReqPayment;
import uz.sfera.edu_platform.payload.res.ResPageable;
import uz.sfera.edu_platform.payload.res.ResPayment;
import uz.sfera.edu_platform.repository.PaymentRepository;
import uz.sfera.edu_platform.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;


    public ApiResponse createRipPayment(PaymentStatusEnum paymentStatus,
                                        PaymentEnum paymentType,
                                        ReqPayment reqPayment) {
        User user = userRepository.findById(reqPayment.getUserId())
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Student"))));

        if (paymentType == PaymentEnum.TUSHUM && user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        if (user.getRole() == Role.ROLE_STUDENT && paymentType == PaymentEnum.CHIQIM) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Student faqat tulov qila oladi"));
        }

        if (paymentType == PaymentEnum.CHIQIM && paymentStatus == null) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Status null bulishi mumkin emas"));
        }

        Payment payment = Payment.builder()
                .student(paymentStatus == PaymentStatusEnum.OYLIK ? null : user)
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
        Double tushum = Optional.ofNullable(paymentRepository.countPrice(PaymentEnum.TUSHUM)).orElse(0.0);
        Double chiqim = Optional.ofNullable(paymentRepository.countPrice(PaymentEnum.CHIQIM)).orElse(0.0);

        ResPayment resPayment = ResPayment.builder()
                .countAllStudent(studentCount)
                .tulovQilganStudent(countStudentsHasPaid)
                .tulovQilmaganStudent(Math.max(studentCount - countStudentsHasPaid, 0))
                .tushum(tushum)
                .chiqim(chiqim)
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
                        .fullName(Optional.ofNullable(payment.getStudent()).map(User::getFullName).orElse("Noma’lum"))
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


    public ApiResponse updatePayment(Long paymentId, PaymentStatusEnum paymentStatus,
                                     PaymentEnum paymentType, ReqPayment reqPayment) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Payment"))));

        User user = userRepository.findById(reqPayment.getUserId())
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Student"))));

        if (paymentStatus == null) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Payment status cannot be null"));
        }
        if (paymentType == null) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Payment type cannot be null"));
        }

        payment.setPaymentStatus(paymentStatus);
        payment.setPaymentType(paymentType);
        payment.setPaymentDate(reqPayment.getPaymentDate());
        payment.setStudent(user);
        payment.setPrice(reqPayment.getPrice());

        paymentRepository.save(payment);
        return new ApiResponse("Successfully updated");
    }


    public ApiResponse deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(ResponseError.NOTFOUND("Payment"))));

        paymentRepository.delete(payment);
        return new ApiResponse("Successfully deleted");
    }


    public ApiResponse  getStatistic(){
        return new ApiResponse(paymentRepository.getMonthlyFinanceReport());
    }
}
