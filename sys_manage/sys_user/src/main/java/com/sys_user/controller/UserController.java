package com.sys_user.controller;

import com.sys_user.entity.UpdateRequest;
import com.sys_user.feign.AuthClient;
import com.sys_user.feign.DataBaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys_user")
public class UserController {
    @Autowired
    private AuthClient authClient;
    @Autowired
    private DataBaseClient dataBaseClient;
    @PostMapping("/updatePW")
    public int updatePW(@RequestParam String password,@RequestHeader("Authorization") String token){
        PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        UpdateRequest updateRequest=new UpdateRequest();
        updateRequest.setUsername(authClient.getUsername(token));
        updateRequest.setPassword(passwordEncoder.encode(password));
        return dataBaseClient.update(updateRequest);
    }
}
