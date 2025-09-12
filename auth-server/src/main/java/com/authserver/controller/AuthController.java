package com.authserver.controller;

import com.authserver.feign.DatabaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    DatabaseClient databaseClient;
    @PostMapping("/login")
    public String login(){
        System.out.println(11111);
        return databaseClient.selectByUname();}
}
