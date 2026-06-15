package com.kawai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Bật Scheduler cho cleanupStaleHolds() trong BookingServiceImpl
public class KawaiApplication {
    public static void main(String[] args) {
        SpringApplication.run(KawaiApplication.class, args);
    }
}
