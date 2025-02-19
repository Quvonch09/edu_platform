package com.example.edu_platform.payload.res;

import java.time.LocalDateTime;

public interface ResStudent {
    Long getId();
    String getFullName();
    String getPhoneNumber();
    Long getGroupId();
    LocalDateTime getCreatedAt();
    Integer getAge();
    String getStatus();
    String getParentPhoneNumber();
    Boolean getHasPaid();
}
