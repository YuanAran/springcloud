package com.sys_user.controller;

import com.sys_user.entity.UpdateRequest;
import com.sys_user.feign.AuthClient;
import com.sys_user.feign.DataBaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sys_user")
public class UserController {
    @Autowired
    private AuthClient authClient;
    @Autowired
    private DataBaseClient dataBaseClient;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserController(PasswordEncoder encoder){
        this.passwordEncoder=encoder;
    }
    //修改密码
    @PostMapping("/updatePW")
    public int updatePW(@RequestBody Map<String, String> body, @RequestHeader("Authorization") String token){
        UpdateRequest updateRequest=new UpdateRequest();
        String password=body.get("password");
        updateRequest.setUsername(authClient.getUsername(token));
        updateRequest.setPassword(passwordEncoder.encode(password));
        return dataBaseClient.update(updateRequest);
    }

    //修改用户信息
    @PostMapping("/update")
    public int update(@RequestBody UpdateRequest updateRequest){
        if (!updateRequest.getPassword().isEmpty()){
            updateRequest.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        return dataBaseClient.update(updateRequest);
    }

    //删除用户
    @GetMapping("/delete")
    public int delete(@RequestParam("username") String username){
        return dataBaseClient.delete(username);
    }
}
