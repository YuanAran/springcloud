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

    //查询用户是否存在
    public SysUser selectByUname(String username) {
        QueryWrapper<SysUser> wrapper =new QueryWrapper<SysUser>()
                .select("username","password")
                .eq("username",username);
        return userMapper.selectOne(wrapper);
    }
    //插入用户
    public int insert(SysUser sysUser) {
        return userMapper.insert(sysUser);
    }
    //更新用户
    public int update(SysUser sysUser) {
        return userMapper.updateById(sysUser);
    }

    //删除用户
    public int delete(String username) {
        QueryWrapper<SysUser> wrapper =new QueryWrapper<SysUser>()
                .eq("username",username);
        return userMapper.delete(wrapper);
    }
}
