package com.databaseprovider.controller;

import com.databaseprovider.entity.LoginRequest;
import com.databaseprovider.entity.UpdateRequest;
import com.databaseprovider.pojo.SysUser;
import com.databaseprovider.service.SqlService;
import com.databaseprovider.until.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/update")
    public int update(@RequestBody UpdateRequest updateRequest){
        SysUser sysUser=new SysUser();
        BeanCopyUtils.copyNonNullProperties(updateRequest,sysUser);
        return sqlService.update(sysUser,sysUser.getUsername());
    }

    @GetMapping("/delete")
    public int delete(@RequestParam("username") String username) {
        return sqlService.delete(username);
    }
}