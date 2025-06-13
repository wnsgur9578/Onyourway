package com.ssafy.onyourway;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnyourwayApplication {

    @Value("${kakao.client-id}")
    private String testKakaoClientId;


    public static void main(String[] args) {
        SpringApplication.run(OnyourwayApplication.class, args);
    }

    @PostConstruct
    public void checkProperty() {
        System.out.println("✅ test kakao.client-id from main = " + testKakaoClientId);
    }
}
