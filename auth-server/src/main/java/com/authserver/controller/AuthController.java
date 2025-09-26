package com.authserver.controller;

import com.authserver.entity.LoginRequest;
import com.authserver.entity.RegisterRequest;
import com.authserver.feign.DatabaseClient;
import com.authserver.until.JwtUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
   @Autowired
   private DatabaseClient databaseClient;

   private final PasswordEncoder passwordEncoder;
   @Autowired
   public AuthController(PasswordEncoder encoder){
       this.passwordEncoder=encoder;
   }
    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody LoginRequest loginRequest){
        Map<String,Object> map = new HashMap<>();

        // 查询用户
        LoginRequest userFromDb = databaseClient.selectByUname(loginRequest.getUsername());
        if (userFromDb == null) {
            map.put("mes", "用户不存在");
            return map;
        }

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), userFromDb.getPassword())) {
            map.put("mes", "密码错误");
            return map;
        }

        // 登录成功，生成token
        String token = JwtUntil.generateToken(loginRequest.getUsername());
        map.put("token", token);
        map.put("mes", "登录成功");

        return map;
    }



    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest){
        if (databaseClient.selectByUname(registerRequest.getUsername())!=null) return "用户已存在";
        int result=databaseClient.insert(registerRequest.getUsername()
                ,passwordEncoder.encode(registerRequest.getPassword())
                ,registerRequest.getSex()
                ,registerRequest.getNickname());
        if (result==0) return "注册失败";
        return "注册成功";
    }
    @GetMapping("/isTokenValid")
    public boolean isTokenValid(@RequestParam("token") String token){
        return JwtUntil.isTokenValid(token);
    }

    @GetMapping ("/getUsername")
    public String getUsername(@RequestHeader("Authorization") String token){
        return JwtUntil.getUsername(token);
    }

}
