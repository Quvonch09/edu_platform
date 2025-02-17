package com.example.edu_platform.service;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.payload.res.ResCEOStatistic;
import com.example.edu_platform.repository.CategoryRepository;
import com.example.edu_platform.repository.GroupRepository;
import com.example.edu_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public ApiResponse getNewStudent(){
        return null;
    }



}
