package com.sys_role.feign;

import com.commonentity.pojo.SysRole;
import com.commonentity.pojo.SysRoleMenu;
import com.commonentity.pojo.SysRoleDept;
import com.commonentity.pojo.SysUserRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //为角色分配菜单权限
    @PostMapping("/db/role/menu/insert")
    int insertRoleMenu(@RequestBody SysRoleMenu sysRoleMenu);

    //删除角色的菜单权限
    @PostMapping("/db/role/menu/delete")
    int deleteRoleMenu(@RequestParam("roleId") String roleId, @RequestParam("menuId") String menuId);

    //删除角色的所有菜单权限
    @PostMapping("/db/role/menu/deleteAll")
    int deleteAllRoleMenus(@RequestParam("roleId") String roleId);

    //为角色分配部门数据权限
    @PostMapping("/db/role/dept/insert")
    int insertRoleDept(@RequestBody SysRoleDept sysRoleDept);

    //删除角色的部门数据权限
    @PostMapping("/db/role/dept/delete")
    int deleteRoleDept(@RequestParam("roleId") String roleId, @RequestParam("deptCode") String deptCode);

    //删除角色的所有部门数据权限
    @PostMapping("/db/role/dept/deleteAll")
    int deleteAllRoleDepts(@RequestParam("roleId") String roleId);

    //为用户分配角色
    @PostMapping("/db/role/user/insert")
    int insertUserRole(@RequestBody SysUserRole sysUserRole);

    //删除用户的角色
    @PostMapping("/db/role/user/delete")
    int deleteUserRole(@RequestParam("userId") String userId, @RequestParam("roleId") String roleId);

    //删除用户的所有角色
    @PostMapping("/db/role/user/deleteAll")
    int deleteAllUserRoles(@RequestParam("userId") String userId);

    //==================== 门面模式：聚合查询接口 ====================

    //获取角色详情（门面接口 - 包含完整权限信息）
    @GetMapping("/db/role/detail")
    RoleDetailVO getRoleDetail(@RequestParam("roleId") String roleId);

    //获取角色权限（门面接口 - 轻量级，只返回ID列表）
    @GetMapping("/db/role/permission")
    RolePermissionVO getRolePermission(@RequestParam("roleId") String roleId);

    //批量获取角色权限（门面接口 - 用于角色列表页）
    @PostMapping("/db/role/permissions/batch")
    List<RolePermissionVO> batchGetRolePermissions(@RequestBody List<String> roleIds);
}
