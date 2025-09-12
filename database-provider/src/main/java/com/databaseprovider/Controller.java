package com.databaseprovider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/db")
public class Controller {
    @GetMapping("/hello")
    public String hello() {
        return "Hello from database-provider!";
    }
}