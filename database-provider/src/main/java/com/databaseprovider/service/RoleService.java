package com.databaseprovider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.commonentity.pojo.SysRole;
import com.databaseprovider.mapper.RoleMapper;
import com.databaseprovider.mapper.RoleMenuMapper;
import com.databaseprovider.mapper.RoleDeptMapper;
import com.databaseprovider.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    
    @Autowired
    private RoleDeptMapper roleDeptMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;

    //获取全部角色
    public List<SysRole> selectList(){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<SysRole>()
                .select("role_id","role_name","role_code","description","status","data_scope","create_time","dept_ids");
        return roleMapper.selectList(wrapper);
    }


    //新增角色
    public int insert(SysRole sysRole) {return roleMapper.insert(sysRole);}
    
    //更新角色
    public int update(SysRole sysRole){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<SysRole>()
                .eq("role_id", sysRole.getRoleId());
        return roleMapper.update(sysRole, wrapper);
    }

    //删除角色
    public int delete(List<String> roleIds) {
        int totalDeleted = 0;
        for (String roleId : roleIds) {
            QueryWrapper<SysRole> wrapper = new QueryWrapper<SysRole>()
                    .eq("role_id", roleId);
            totalDeleted += roleMapper.delete(wrapper);
        }
        return totalDeleted;
    }

}
