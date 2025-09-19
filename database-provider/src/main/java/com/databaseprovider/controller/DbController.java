package com.databaseprovider.controller;

import com.databaseprovider.entity.LoginRequest;
import com.databaseprovider.pojo.SysUser;
import com.databaseprovider.service.SqlService;
import com.google.common.hash.HashCode;
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
        if(sysUser==null)return null;
        loginRequest.setUsername(sysUser.getUsername());
        loginRequest.setPassword(sysUser.getPassword());
        return loginRequest;
    }
    @GetMapping("/insert")
    public int insert(@RequestParam("username") String username,@RequestParam("password") String password ,@RequestParam("sex") long sex,@RequestParam("nickname") String nickname) {
        SysUser sysUser=new SysUser();
        sysUser.setUsername(username);
        sysUser.setPassword(password);
        sysUser.setSex(sex);
        sysUser.setCreateTime(new java.sql.Timestamp(new java.util.Date().getTime()));
        sysUser.setUserId(username);
        sysUser.setNickname( nickname);
        return sqlService.insert(sysUser);
    }
}