package com.example.edu_platform.service;

import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.ResponseError;
import com.example.edu_platform.payload.res.*;
import com.example.edu_platform.repository.CategoryRepository;
import com.example.edu_platform.repository.GroupRepository;
import com.example.edu_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {


    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;

    public ApiResponse getCEOStatistics() {

        ResCEOStatistic statistic = new ResCEOStatistic();

        statistic.setTeacherCount(userRepository.countAllByTeacher());
        statistic.setStudentCount(userRepository.countAllByStudent());
        statistic.setGroupCount(groupRepository.countAllByGroup());
        statistic.setCategoryCount(categoryRepository.countAllByCategory());

        return new ApiResponse(statistic);

    }

    public ApiResponse getAdminStatistics() {

        ResAdminStatistic statistic = new ResAdminStatistic();

        statistic.setTeacherCount(userRepository.countAllByTeacher());
        statistic.setStudentCount(userRepository.countAllByStudent());
        statistic.setGroupCount(groupRepository.countAllByGroup());
        statistic.setCategoryCount(categoryRepository.countAllByCategory());

        return new ApiResponse(statistic);

    }


    public ApiResponse getTeacherStatistics(User user) {

        ResTeacherStatistic statistic = new ResTeacherStatistic();

        statistic.setStudentCount(userRepository.countAllByStudent(user.getId()));
        statistic.setGroupCount(groupRepository.countAllByGroup(user.getId()));

        return new ApiResponse(statistic);

    }


    public ApiResponse getStudentStatistics(User user) {
        ResStudentStatistic statistic = groupRepository.findGroupByStudentId(user.getId());

        if (statistic == null){
            return new ApiResponse(ResponseError.NOTFOUND("Statistic not found"));
        }
        return new ApiResponse(statistic);
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
