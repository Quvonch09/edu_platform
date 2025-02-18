package com.example.edu_platform.payload.res;

import java.time.LocalDateTime;

public interface ResStudent {
    Long getId();
    String getFullName();
    String getPhoneNumber();
    Long getGroupId();
    LocalDateTime getStartStudyDate();
    Integer getAge();
    String getStatus();
    String getParentPhoneNumber();
}
