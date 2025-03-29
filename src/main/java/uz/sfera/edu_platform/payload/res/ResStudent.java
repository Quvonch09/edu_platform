package uz.sfera.edu_platform.payload.res;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ResStudent {
    Long getId();
    String getFullName();
    String getPhoneNumber();
    String getGroupName();
    Long getGroupId();
    Long getChatId();
    String getTeacherName();
    LocalDateTime getCreatedAt();
    Integer getAge();
    String getStatus();
    LocalDate getDepartureDate();
    String getDepartureDescription();
    String getParentPhoneNumber();
    Boolean getHasPaid();
    Boolean getIsStarted();
    Double getScore();
}
