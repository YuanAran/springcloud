package com.sys_role.feign;

import com.commonentity.pojo.SysRole;
import com.commonentity.pojo.SysRoleMenu;
import com.commonentity.pojo.SysRoleDept;
import com.commonentity.vo.RoleVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("database-provider")
public interface DatabaseClient {
    //获取全部角色
    @GetMapping("/db/role/selectList")
    List<SysRole> selectList();

    //新增角色
    @PostMapping("/db/role/insert")
    int insert(@RequestBody SysRole sysRole);

    //更新角色
    @PostMapping("/db/role/update")
    int update(@RequestBody SysRole sysRole);

    //删除角色
    @PostMapping("/db/role/delete")
    int delete(@RequestBody List<String> roleIds);

    @PostMapping("/db/role/selectRoleVo")
    RoleVo selectRoleVo(@RequestBody Map<String,String> map);


    /**
     * 权限部门增删改
     * */
    @PostMapping("/db/role/insertRoleDept")
     int insertRoleDept(@RequestBody List<SysRoleDept> list);


    @PostMapping("/db/role/deleteRoleDept")
     int deleteRoleDept(@RequestBody List<SysRoleDept> roleDepts);

    /**
     * 角色菜单增删改
     * */
    @PostMapping("/db/role/insertRoleMenu")
     int insertRoleMenu(@RequestBody List<SysRoleMenu> list);

    @PostMapping("/db/role/deleteRoleMenu")
     int deleteRoleMenu(@RequestBody List<SysRoleMenu> roleMenus);
}
