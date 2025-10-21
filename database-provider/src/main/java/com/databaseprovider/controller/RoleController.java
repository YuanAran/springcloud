package com.databaseprovider.controller;

import com.commonentity.pojo.SysRole;
import com.databaseprovider.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
