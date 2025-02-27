package uz.sfera.edu_platform.payload.res;

import lombok.*;

import java.time.LocalTime;


public interface ResRoom {
    Long getId();
    String getName();
    String getColor();
    String getStartTime();
    String getEndTime();
}
