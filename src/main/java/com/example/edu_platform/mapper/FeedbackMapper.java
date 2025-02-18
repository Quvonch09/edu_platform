package com.example.edu_platform.mapper;

import com.example.edu_platform.entity.Feedback;
import com.example.edu_platform.payload.ResponseFeedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FeedbackMapper {


    @Mapping(source = "id", target = "id")
    @Mapping(source = "feedback", target = "feedback")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "student.fullName", target = "studentName")
    @Mapping(source = "createdAt", target = "feedbackTime")
    ResponseFeedback toResponseFeedback(Feedback feedback);
}
