package com.databaseprovider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/db")
public class DbController {
    @GetMapping("/selectByUname")
    public String selectByUname() {
        return "Login from database-provider!";
    }
}