package com.databaseprovider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@MapperScan("com.databaseprovider.mapper")
public class DatabaseProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseProviderApplication.class, args);
    }

}
