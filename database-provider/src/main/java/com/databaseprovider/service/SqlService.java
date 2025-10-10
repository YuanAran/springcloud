package com.databaseprovider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.databaseprovider.mapper.UserMapper;
import com.commonentity.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SqlService {
    @Autowired
    private UserMapper userMapper;

    //查询用户是否存在
    public SysUser selectByUname(String username) {
        QueryWrapper<SysUser> wrapper =new QueryWrapper<SysUser>()
                .select("username","password","user_id")
                .eq("username",username);
        return userMapper.selectOne(wrapper);
    }
    //插入用户
    public int insert(SysUser sysUser) {
        return userMapper.insert(sysUser);
    }
    //更新用户
    public int update(SysUser sysUser,String username) {
        QueryWrapper<SysUser> wrapper =new QueryWrapper<SysUser>()
                .eq("username",username);
        return userMapper.update(sysUser,wrapper);
    }

    //删除用户
    public int delete(List<String> usernames) {
        int totalDeleted = 0;
        for (String username : usernames) {
            QueryWrapper<SysUser> wrapper = new QueryWrapper<SysUser>()
                    .eq("username", username);
            totalDeleted += userMapper.delete(wrapper);
        }
        return totalDeleted;
    }

    //获取全部用户
    public List<SysUser> selectList(){
        QueryWrapper<SysUser> wrapper = new QueryWrapper<SysUser>()
                .select("username", "nickname", "sex", "create_time");
        return userMapper.selectList(wrapper);
    }
}
