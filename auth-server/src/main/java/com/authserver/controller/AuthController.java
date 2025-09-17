package com.authserver.controller;

import com.authserver.entity.LoginRequest;
import com.authserver.feign.DatabaseClient;
import com.authserver.until.JwtUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    DatabaseClient databaseClient;
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){
        System.out.println(loginRequest.getUsername()+","+ loginRequest.getPassword());
        LoginRequest loginRequest1=databaseClient.selectByUname(loginRequest.getUsername());
        if (loginRequest1==null) return "用户不存在";
        if (!loginRequest1.getPassword().equals(loginRequest.getPassword())) return "密码错误";
        return JwtUntil.generateToken(loginRequest.getUsername());}

    @GetMapping("/isTokenValid")
    public boolean isTokenValid(@RequestParam("token") String token){
        return JwtUntil.isTokenValid(token);
    }
}
