package com.databaseprovider.controller;

import com.commonentity.pojo.SysRole;
import com.commonentity.pojo.SysRoleDept;
import com.commonentity.pojo.SysRoleMenu;
import com.commonentity.vo.RoleVo;
import com.databaseprovider.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//角色的crud
@RestController
@RequestMapping("/db/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    //获取全部角色
    @GetMapping("/selectList")
    public List<SysRole> selectList() {
        return roleService.selectList();
    }
    //新增角色
    @PostMapping("/insert")
    public int insert(@RequestBody SysRole sysRole) {
        return roleService.insert(sysRole);
    }
    
    //更新角色
    @PostMapping("/update")
    public int update(@RequestBody SysRole sysRole) {
        return roleService.update(sysRole);
    }

    //删除角色
    @PostMapping("/delete")
    public int delete(@RequestBody List<String> roleIds) {
        return roleService.delete(roleIds);
    }


    /**
     * 聚合查询
     * RequestBody注解:因为前端传的参数是json对象，所以这里用Map接收参数。如果有创建相关类则用实体类接收
     * */
    @PostMapping("/selectRoleVo")
    public RoleVo selectRoleVo(@RequestBody Map<String,String>map){
        return roleService.selectRoleVo(map.get("roleId"));
    }

    /**
     * 权限部门增删改
     * */
    @PostMapping("/insertRoleDept")
    public int insertRoleDept(@RequestBody SysRoleDept sysRoleDept){
        return roleService.insertRoleDept(sysRoleDept);
    }
    @PostMapping("/updateRoleDept")
    public int updateRoleDept(@RequestBody SysRoleDept sysRoleDept){
        return roleService.updateRoleDept(sysRoleDept);
    }

    @PostMapping("/deleteRoleDept")
    public int deleteRoleDept(@RequestBody List<SysRoleDept> roleDepts){
        return roleService.deleteRoleDept(roleDepts);
    }

    /**
     * 角色菜单增删改
     * */
    @PostMapping("/insertRoleMenu")
    public int insertRoleMenu(@RequestBody SysRoleMenu sysRoleMenu){
        return roleService.insertRoleMenu(sysRoleMenu);
    }
    @PostMapping("/updateRoleMenu")
    public int updateRoleMenu(@RequestBody SysRoleMenu sysRoleMenu){
        return roleService.updateRoleMenu(sysRoleMenu);
    }
    @PostMapping("/deleteRoleMenu")
    public int deleteRoleMenu(@RequestBody List<SysRoleMenu> roleMenus){
        return roleService.deleteRoleMenu(roleMenus);
    }
}
