package com.databaseprovider.controller;

import com.databaseprovider.entity.LoginRequest;
import com.databaseprovider.pojo.SysUser;
import com.databaseprovider.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/db")
public class DbController {
    @Autowired
    private SqlService sqlService;
    @GetMapping("/selectByUname")
    public LoginRequest selectByUname(@RequestParam("username") String username) {
        System.out.println("username:"+username);
        LoginRequest loginRequest=new LoginRequest();
        SysUser sysUser=sqlService.selectByUname(username);
        loginRequest.setUsername(sysUser.getUsername());
        loginRequest.setPassword(sysUser.getPassword());
        return loginRequest;
    }
}