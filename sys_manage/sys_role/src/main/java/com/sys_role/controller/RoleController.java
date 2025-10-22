package com.sys_role.controller;

import com.commonentity.pojo.SysRole;
import com.commonentity.pojo.SysRoleDept;
import com.commonentity.pojo.SysRoleMenu;
import com.commonentity.vo.RoleVo;
import com.sys_role.feign.DatabaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sys_role")
public class RoleController {
    @Autowired
    private DatabaseClient databaseClient;

    @GetMapping("/getAllRole")
    public List<SysRole> getAllRole(){
        return databaseClient.selectList();
    }

    @PostMapping("/addRole")
    public int addRole(@RequestBody SysRole sysRole){
        UUID uuid = UUID.randomUUID();
        sysRole.setRoleId(uuid.toString().replace("-",""));
        sysRole.setCreateTime(new Timestamp(new Date().getTime()));
        return databaseClient.insert(sysRole);
    }

    @PostMapping("/updateRole")
    public int updateRole(@RequestBody SysRole sysRole){
        sysRole.setUpdateTime(new Timestamp(new Date().getTime()));
        return databaseClient.update(sysRole);
    }

    @PostMapping("/deleteRole")
    public int deleteRole(@RequestBody List<String> roleIds){
        return databaseClient.delete(roleIds);
    }

    @PostMapping("/getRoleVo")
    public RoleVo getRoleVo(@RequestBody Map<String,String> map){
        return databaseClient.selectRoleVo(map);
    }


    /**
     * 权限部门增删改
     * */
    @PostMapping("/addRoleDept")
    public int addRoleDept(@RequestBody List<SysRoleDept> list){
        return databaseClient.insertRoleDept(list);
    }

    @PostMapping("/updateRoleDept")
    public int updateRoleDept(@RequestBody SysRoleDept sysRoleDept){
        return databaseClient.updateRoleDept(sysRoleDept);
    }

    @PostMapping("/deleteRoleDept")
    public int deleteRoleDept(@RequestBody List<SysRoleDept> roleDepts){
        return databaseClient.deleteRoleDept(roleDepts);
    }

    /**
     * 角色菜单增删改
     * */
    @PostMapping("/addRoleMenu")
    public int addRoleMenu(@RequestBody List<SysRoleMenu> list){
        return databaseClient.insertRoleMenu(list);
    }

    @PostMapping("/updateRoleMenu")
    public int updateRoleMenu(@RequestBody SysRoleMenu sysRoleMenu){
        return databaseClient.updateRoleMenu(sysRoleMenu);
    }

    @PostMapping("/deleteRoleMenu")
    public int deleteRoleMenu(@RequestBody List<SysRoleMenu> roleMenus){
        return databaseClient.deleteRoleMenu(roleMenus);
    }
}
