package uz.sfera.edu_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EduPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduPlatformApplication.class, args);
    }

}
