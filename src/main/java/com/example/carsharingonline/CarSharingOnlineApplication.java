package com.example.carsharingonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
public class CarSharingOnlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarSharingOnlineApplication.class, args);
    }

}
