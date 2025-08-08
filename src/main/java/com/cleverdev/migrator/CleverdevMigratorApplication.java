package com.cleverdev.migrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CleverdevMigratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleverdevMigratorApplication.class, args);
    }

}
