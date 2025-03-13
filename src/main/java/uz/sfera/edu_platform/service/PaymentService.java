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

        User user = null;

        if (reqPayment.getUserId() != null){
            user = userRepository.findById(reqPayment.getUserId()).orElse(null);
        }

        if (paymentType.equals(PaymentEnum.CHIQIM)){
            if (reqPayment.getUserName() == null || reqPayment.getUserName().isEmpty()){
                return new ApiResponse(ResponseError.DEFAULT_ERROR("UserName kiritilsin!!!"));
            }

            Payment payment = Payment.builder()
                    .paid((byte) 1)
                    .userName(reqPayment.getUserName() != null ? reqPayment.getUserName() : null)
                    .paymentDate(reqPayment.getPaymentDate())
                    .paymentStatus(paymentStatus)
                    .paymentType(paymentType)
                    .price(reqPayment.getPrice())
                    .paymentType(paymentType)
                    .student(null)
                    .build();
            paymentRepository.save(payment);

        } else {
            if (user == null){
                return new ApiResponse(ResponseError.NOTFOUND("Student"));
            }

            Payment payment = Payment.builder()
                    .student(user)
                    .userName(user.getFullName())
                    .price(reqPayment.getPrice())
                    .paymentDate(reqPayment.getPaymentDate())
                    .paymentStatus(paymentStatus)
                    .paymentType(paymentType)
                    .paid(reqPayment.isPaid() ? (byte) 1:0)
                    .build();
            paymentRepository.save(payment);
        }

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


    public ApiResponse search(String userName, PaymentStatusEnum paymentStatus, PaymentEnum paymentEnum,
                              Boolean paid, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Byte hasPaid = (paid == null) ? null : (byte) (paid ? 1 : 0);

        Page<Payment> payments = paymentRepository.searchPayments(
                userName, hasPaid, Optional.ofNullable(paymentStatus).map(Enum::name).orElse(null),
                Optional.ofNullable(paymentEnum).map(Enum::name).orElse(null), pageRequest
        );

        if (payments.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Paymentlar"));
        }

        List<PaymentDTO> paymentDTOList = payments.stream()
                .map(payment -> PaymentDTO.builder()
                        .id(payment.getId())
                        .fullName(payment.getUserName())
                        .paymentDate(payment.getPaymentDate())
                        .paymentStatus(payment.getPaymentStatus())
                        .price(payment.getPrice())
                        .paid(payment.getPaid() == 1 ? true : false)
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
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null){
            return new ApiResponse(ResponseError.NOTFOUND("Payment"));
        }

        User user = null;

        if (reqPayment.getUserId() != null){
            user = userRepository.findById(reqPayment.getUserId()).orElse(null);
        }


        if (paymentType == null) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Payment type cannot be null"));
        }

        payment.setPaymentStatus(paymentStatus);

        if (user.getRole().equals(Role.ROLE_STUDENT)){
            if (user == null){
                return new ApiResponse(ResponseError.NOTFOUND("Student"));
            }
            payment.setUserName(user.getFullName());
            payment.setStudent(user);
        }
        payment.setUserName(reqPayment.getUserName() != null ? reqPayment.getUserName() : null);
        payment.setPaymentType(paymentType);
        payment.setPaymentDate(reqPayment.getPaymentDate());
        payment.setPrice(reqPayment.getPrice());
        payment.setPaid(reqPayment.isPaid() ? (byte) 1:0);

        paymentRepository.save(payment);
        return new ApiResponse("Successfully updated");
    }


    public ApiResponse deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Payment"));
        }
        paymentRepository.delete(payment);
        return new ApiResponse("Successfully deleted");
    }


    public ApiResponse  getStatistic(){
        return new ApiResponse(paymentRepository.getMonthlyFinanceReport());
    }
}
