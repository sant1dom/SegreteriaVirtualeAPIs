package com.beyondrest.SegreteriaVirtualeGraphQL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableMethodSecurity
public class SegreteriaVirtualeSpring {
    public static void main(String[] args) {
        SpringApplication.run(SegreteriaVirtualeSpring.class, args);
    }
}
