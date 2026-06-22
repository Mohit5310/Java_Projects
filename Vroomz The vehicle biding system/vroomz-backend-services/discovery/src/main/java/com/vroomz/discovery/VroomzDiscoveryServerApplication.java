package com.vroomz.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer // Iske bina server kaam nahi karega
public class VroomzDiscoveryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(VroomzDiscoveryServerApplication.class, args);
    }
}