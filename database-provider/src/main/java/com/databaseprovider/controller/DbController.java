package com.databaseprovider.controller;

import com.commonentity.entity.LoginRequest;
import com.databaseprovider.entity.UpdateRequest;
import com.commonentity.pojo.SysUser;
import com.databaseprovider.service.SqlService;
import com.databaseprovider.until.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/db")
public class DbController {
    @Autowired
    private SqlService sqlService;
    @GetMapping("/selectByUname")
    public LoginRequest selectByUname(@RequestParam("username") String username) {
        LoginRequest loginRequest=new LoginRequest();
        SysUser sysUser=sqlService.selectByUname(username);
        if(sysUser==null)return null;
        loginRequest.setUsername(sysUser.getUsername());
        loginRequest.setPassword(sysUser.getPassword());
        return loginRequest;
    }
    @PostMapping("/insert")
    public int insert(@RequestBody SysUser sysUser) {
        PasswordEncoder encoder=new BCryptPasswordEncoder();
        UUID uuid=UUID.randomUUID();
        sysUser.setPassword(encoder.encode(sysUser.getPassword()));
        sysUser.setUserId(uuid.toString().replace("-",""));
        sysUser.setCreateTime(new Timestamp(new Date().getTime()));
        return sqlService.insert(sysUser);
    }

    @PostMapping("/update")
    public int update(@RequestBody UpdateRequest updateRequest){
        SysUser sysUser=new SysUser();
        BeanCopyUtils.copyNonNullProperties(updateRequest,sysUser);
        return sqlService.update(sysUser,sysUser.getUsername());
    }

    @PostMapping("/delete")
    public int delete(@RequestBody List<String> usernames) {
        return sqlService.delete(usernames);
    }
    @GetMapping("/selectList")
    public List<SysUser> selectList(){return sqlService.selectList();}
}