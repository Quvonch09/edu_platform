package com.example.edu_platform.service;

import com.example.edu_platform.entity.Payment;
import com.example.edu_platform.entity.User;
import com.example.edu_platform.entity.enums.PaymentEnum;
import com.example.edu_platform.entity.enums.PaymentStatusEnum;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.PaymentDTO;
import com.example.edu_platform.payload.res.ResPageable;
import com.example.edu_platform.payload.res.ResPayment;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.req.ReqPayment;
import com.example.edu_platform.repository.PaymentRepository;
import com.example.edu_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        if (paymentType.equals(PaymentEnum.CHIQIM)){
            if (paymentStatus == null){
                return new ApiResponse(ResponseError.DEFAULT_ERROR("Status null bulishi mumkin emas"));
            }
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


    public ApiResponse getPaymentCount(){
        Integer studentCount = userRepository.countAllByStudent();
        Integer countStudentsHasPaid = userRepository.countStudentsHasPaid();

        ResPayment resPayment = ResPayment.builder()
                .countAllStudent(studentCount)
                .tulovQilganStudent(countStudentsHasPaid)
                .tulovQilmaganStudent(studentCount-countStudentsHasPaid)
                .tushum(paymentRepository.countPrice(PaymentEnum.TUSHUM))
                .chiqim(paymentRepository.countPrice(PaymentEnum.CHIQIM))
                .build();
        return new ApiResponse(resPayment);
    }


    public ApiResponse search(String userName,PaymentStatusEnum paymentStatus, int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Payment> payments = paymentRepository.searchPayments(userName,paymentStatus != null
                        ? paymentStatus.name() : null
                ,pageRequest);

        List<PaymentDTO> paymentDTOList = new ArrayList<>();
        for (Payment payment : payments) {
            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .id(payment.getId())
                    .fullName(payment.getStudent().getFullName())
                    .paymentDate(payment.getPaymentDate())
                    .paymentStatus(payment.getPaymentStatus())
                    .price(payment.getPrice())
                    .build();
            paymentDTOList.add(paymentDTO);
        }

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

        payment.setPaymentStatus(paymentStatus);
        payment.setPaymentType(paymentType);
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
}
