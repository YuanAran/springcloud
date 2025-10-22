package com.databaseprovider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.commonentity.pojo.*;
import com.commonentity.vo.RoleVo;
import com.databaseprovider.mapper.RoleMapper;
import com.databaseprovider.mapper.RoleMenuMapper;
import com.databaseprovider.mapper.RoleDeptMapper;
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

    /**
     * 聚合查询
     * */
    public RoleVo selectRoleVo(String roleId){
        RoleVo roleVo = new RoleVo();
        roleVo.setDepts(roleDeptMapper.getRoleDepts(roleId));
        roleVo.setMenus(roleMenuMapper.getRoleMenus(roleId));
        return roleVo;
    }

    /**
     * 关于权限部门的增删改
     * */
    //增加角色的权限部门
    public int insertRoleDept(SysRoleDept sysRoleDept){
        return roleDeptMapper.insert(sysRoleDept);
    }

    public int updateRoleDept(SysRoleDept sysRoleDept){
        QueryWrapper<SysRoleDept> wrapper = new QueryWrapper<SysRoleDept>()
                .eq("role_id", sysRoleDept.getRoleId())
                .eq("dept_code", sysRoleDept.getDeptCode());
        return roleDeptMapper.update(sysRoleDept, wrapper);
    }

    public int deleteRoleDept(List<SysRoleDept> list){
        int totalDeleted = 0;
        for(SysRoleDept sysRoleDept:list){
            QueryWrapper<SysRoleDept> wrapper = new QueryWrapper<SysRoleDept>()
                    .eq("role_id",sysRoleDept.getRoleId())
                    .eq("dept_code", sysRoleDept.getDeptCode());
            totalDeleted += roleDeptMapper.delete(wrapper);
        }

        return totalDeleted;
    }


    /**
     * 关于权限菜单的增删改
     * */

    //插入
     public int insertRoleMenu(SysRoleMenu sysRoleMenu){
        return roleMenuMapper.insert(sysRoleMenu);
    }

    //更新
    public int updateRoleMenu(SysRoleMenu sysRoleMenu){
        QueryWrapper<SysRoleMenu> wrapper = new QueryWrapper<SysRoleMenu>()
                .eq("role_id", sysRoleMenu.getRoleId())
                .eq("menu_id", sysRoleMenu.getMenuId());
        return roleMenuMapper.update(sysRoleMenu, wrapper);
    }

    //删除
    public int deleteRoleMenu(List<SysRoleMenu> list){
        int totalDeleted = 0;
        for(SysRoleMenu sysRoleMenu:list){
            QueryWrapper<SysRoleMenu> wrapper = new QueryWrapper<SysRoleMenu>()
                    .eq("role_id",sysRoleMenu.getRoleId())
                    .eq("menu_id", sysRoleMenu.getMenuId());
            totalDeleted += roleMenuMapper.delete(wrapper);
        }
        return totalDeleted;
    }
}
