package com.databaseprovider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.databaseprovider.mapper.UserMapper;
import com.databaseprovider.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SqlService {
    @Autowired
    private UserMapper userMapper;

    public SysUser selectByUname(String username) {
        QueryWrapper<SysUser> wrapper =new QueryWrapper<SysUser>()
                .select("username","password")
                .eq("username",username);
        return userMapper.selectOne(wrapper);
    }
}
