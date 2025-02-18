package com.example.edu_platform.controller;


import com.example.edu_platform.entity.User;
import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.security.CurrentUser;
import com.example.edu_platform.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/new-student")
    @PreAuthorize("hasAnyRole('ROLE_CEO' , 'ROLE_ADMIN' , 'ROLE_TEACHER')")
    @Operation(summary = " Yangi studentlar soni diagramma uchun CEO , ADMIN , TEACHER ga  ")
    public ResponseEntity<ApiResponse> getNewStudentStatistic(){
        return ResponseEntity.ok(statisticService.getNewStudent());
    }


    @Operation(summary = "Yangi guruhlar soni diagramma uchun CEO , ADMIN , TEACHER ga")
    @PreAuthorize("hasAnyRole('ROLE_CEO' , 'ROLE_ADMIN' ,'ROLE_TEACHER')")
    @GetMapping("/new-group")
    public ResponseEntity<ApiResponse> getNewGroupStatistic(){
        return ResponseEntity.ok(statisticService.getNewGroup());
    }


    @Operation(summary = "Oylik chiqib ketgan o'quvchilar diagramma CEO , ADMIN , TEACHER ga")
    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN','ROLE_TEACHER')")
    @GetMapping("/leave-student")
    public ResponseEntity<ApiResponse> getLeaveStudentStatistic(){
        return ResponseEntity.ok(statisticService.getLeaveStudentStatistic());
    }


    @Operation(summary = "1-oy ,2-oy , 3-oy tugashi mumkin bulgan guruhlar soni CEO va ADMIN ga")
    @PreAuthorize("hasAnyRole('ROLE_CEO','ROLE_ADMIN','ROLE_TEACHER')")
    @GetMapping("/group-endDate")
    public ResponseEntity<ApiResponse> getGroupEndDateStatistic(){
        return ResponseEntity.ok(statisticService.getGroupEndDateStatistic());
    }

    @Operation(summary =" CEO uchun sonlarda statistika  ")
    @PreAuthorize("hasAnyRole('ROLE_CEO')")
    @GetMapping("/ceo-dashboard")
    public ResponseEntity<ApiResponse> getCeoDashboardStatistic(){
        return ResponseEntity.ok(statisticService.getCEOStatistics());
    }


    @Operation(summary =" Admin uchun sonlarda statistika  ")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/admin-dashboard")
    public ResponseEntity<ApiResponse> getAdminDashboardStatistic(){
        return ResponseEntity.ok(statisticService.getAdminStatistics());
    }

    @Operation(summary =" Teacher uchun sonlarda statistika  ")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER')")
    @GetMapping("/teacher-dashboard")
    public ResponseEntity<ApiResponse> getAdminDashboardStatistic(@CurrentUser User user){
        return ResponseEntity.ok(statisticService.getTeacherStatistics(user));
    }

    @Operation(summary =" Student uchun sonlarda statistika  ")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    @GetMapping("/student-dashboard")
    public ResponseEntity<ApiResponse> getStudentDashboardStatistic(@CurrentUser User user){
        return ResponseEntity.ok(statisticService.getStudentStatistics(user));
    }


    @Operation(summary =" Student guruhidagi reyting jadvali  ")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    @GetMapping("/student-rank")
    public ResponseEntity<ApiResponse> getStudentGroupRank(@CurrentUser User user){
        return ResponseEntity.ok(statisticService.getStudentRank(user));
    }




}
