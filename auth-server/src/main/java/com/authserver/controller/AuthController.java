package com.authserver.controller;

import com.authserver.entity.LoginRequest;
import com.authserver.entity.RegisterRequest;
import com.authserver.feign.DatabaseClient;
import com.authserver.until.JwtUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    DatabaseClient databaseClient;
    PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){
        LoginRequest loginRequest1=databaseClient.selectByUname(loginRequest.getUsername());
        if (loginRequest1==null) return "用户不存在";
        if (!passwordEncoder.matches(loginRequest.getPassword(),loginRequest1.getPassword())) return "密码错误";
        return JwtUntil.generateToken(loginRequest.getUsername());}


    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest){
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        if (databaseClient.selectByUname(registerRequest.getUsername())!=null) return "用户已存在";
        int result=databaseClient.insert(registerRequest.getUsername(),registerRequest.getPassword(),registerRequest.getSex(),registerRequest.getNickname());
        if (result==0) return "注册失败";
        return "注册成功";
    }
    @GetMapping("/isTokenValid")
    public boolean isTokenValid(@RequestParam("token") String token){
        return JwtUntil.isTokenValid(token);
    }
}
