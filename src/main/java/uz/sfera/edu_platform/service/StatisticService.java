package uz.sfera.edu_platform.service;

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


    //todo bu annotatsiyani olib tashla degandim
    @Transactional
    public ApiResponse getStudentStatisticByGroup(Long groupId, User user) {
        Group group;
        if (user.getRole().equals(Role.ROLE_STUDENT)){
           group = groupRepository.findByStudentId(user.getId()).orElse(null);
        }else {
            group = groupRepository.findById(groupId).orElse(null);
        }

        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        List<ResStudentRank> allByStudentRank = new ArrayList<>();
        for (User student : group.getStudents()) {
            allByStudentRank.addAll(groupRepository.findAllByStudentRank(student.getId()));
        }

        return new ApiResponse(allByStudentRank);
    }


    public ApiResponse getStudentRank(User user) {
        List<ResStudentRank> ranks = groupRepository.findAllByStudentRank(user.getId());

        if (ranks.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Rank not found"));
        }
        return new ApiResponse(ranks);
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
