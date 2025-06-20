package com.amps.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class IpuUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(IpuUserApplication.class, args);
    }

}
