package com.example.edu_platform.service;

import com.example.edu_platform.payload.ApiResponse;
import com.example.edu_platform.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

}
