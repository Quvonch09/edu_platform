package uz.sfera.edu_platform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.sfera.edu_platform.entity.Group;
import uz.sfera.edu_platform.entity.User;
import uz.sfera.edu_platform.entity.enums.PaymentEnum;
import uz.sfera.edu_platform.entity.enums.Role;
import uz.sfera.edu_platform.payload.ApiResponse;
import uz.sfera.edu_platform.payload.ResponseError;
import uz.sfera.edu_platform.payload.res.*;
import uz.sfera.edu_platform.repository.CategoryRepository;
import uz.sfera.edu_platform.repository.GroupRepository;
import uz.sfera.edu_platform.repository.PaymentRepository;
import uz.sfera.edu_platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@Service
@RequiredArgsConstructor
public class StatisticService {


    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;

    public ApiResponse getCEOStatistics() {

        ResCEOStatistic statistic = new ResCEOStatistic();

        statistic.setTeacherCount(userRepository.countAllByTeacher());
        statistic.setStudentCount(userRepository.countAllByStudent());
        statistic.setGroupCount(groupRepository.countAllByGroup());
        statistic.setCategoryCount(categoryRepository.countAllByCategory());
        statistic.setInCome(paymentRepository.countPrice(PaymentEnum.CHIQIM));
        statistic.setOutCome(paymentRepository.countPrice(PaymentEnum.TUSHUM));
        statistic.setAvgMonPayment(paymentRepository.avgPayment());
        statistic.setPaidAllCount(userRepository.countAllByStudent());
        statistic.setPaidCount(userRepository.countStudentsHasPaid());


        return new ApiResponse(statistic);

    }

    public ApiResponse getAdminStatistics() {

        ResAdminStatistic statistic = new ResAdminStatistic();

        statistic.setTeacherCount(userRepository.countAllByTeacher());
        statistic.setStudentCount(userRepository.countAllByStudent());
        statistic.setGroupCount(groupRepository.countAllByGroup());
        statistic.setCategoryCount(categoryRepository.countAllByCategory());
        statistic.setPaidCount(userRepository.countStudentsHasPaid());
        statistic.setPaidAllCount(userRepository.countAllByStudent());

        return new ApiResponse(statistic);

    }


    public ApiResponse getTeacherStatistics(User user) {

        ResTeacherStatistic statistic = new ResTeacherStatistic();

        statistic.setStudentCount(userRepository.countAllByStudent(user.getId()));
        statistic.setGroupCount(groupRepository.countAllByGroup(user.getId()));
        statistic.setPaidCount(groupRepository.countStudentByTeacherId(user.getId()));
        statistic.setPaidAllCount(userRepository.countAllByStudent(user.getId()));

        return new ApiResponse(statistic);

    }


    public ApiResponse getStudentStatistics(User user) {
        ResStudentStatistic statistic = groupRepository.findGroupByStudentId(user.getId());

        if (statistic == null){
            return new ApiResponse(ResponseError.NOTFOUND("Statistic not found"));
        }
        return new ApiResponse(statistic);
    }


    public ApiResponse getStudentStatisticByGroup(Long groupId, User user, int page, int size) {
        Group group = user.getRole().equals(Role.ROLE_STUDENT)
                ? groupRepository.findByStudentId(user.getId()).orElse(null)
                : groupRepository.findById(groupId).orElse(null);

        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> studentsPage = groupRepository.findStudentsByGroupId(group.getId(), pageable);

//        List<ResStudentRank> studentRanks = studentsPage.stream()
//                .flatMap(student -> groupRepository.findAllByStudentRank(student.getId()).stream())
//                .collect(Collectors.toList());
        Page<ResStudentRank> allByStudentRank = null;
        for (User user1 : studentsPage.getContent()) {
            allByStudentRank = groupRepository.findAllByStudentRank(user1.getId(), pageable);
        }

//        Page<ResStudentRank> studentRankPage = new PageImpl<>(studentRanks, pageable, studentsPage.getTotalElements());

        if (allByStudentRank == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Barcha student reytinglari"));
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(allByStudentRank.getTotalPages())
                .totalElements(allByStudentRank.getTotalElements())
                .body(allByStudentRank.getContent())
                .build();

        return new ApiResponse(resPageable);
    }



    public ApiResponse getStudentRank(User user, int page, int size) {
        Page<ResStudentRank> ranks = groupRepository.findAllByStudentRank(user.getId(), PageRequest.of(page, size));

        if (ranks.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Rank not found"));
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(ranks.getTotalPages())
                .totalElements(ranks.getTotalElements())
                .body(ranks.getContent())
                .build();

        return new ApiResponse(resPageable);
    }

    public ApiResponse getNewStudent(){
        return new ApiResponse(userRepository.getCEODiagrams());
    }

    public ApiResponse getNewGroup(){
        return new ApiResponse(groupRepository.findByMonthlyStatistic());
    }

    public ApiResponse getLeaveStudentStatistic(){
        return new ApiResponse(userRepository.getLeaveStudent());
    }

    public ApiResponse getGroupEndDateStatistic(){
        return new ApiResponse(groupRepository.findByGroupEndDate());
    }
}