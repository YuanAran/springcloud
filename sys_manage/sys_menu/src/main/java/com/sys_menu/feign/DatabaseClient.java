package com.sys_menu.feign;

import com.commonentity.pojo.SysMenu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("database-provider")
public interface DatabaseClient {
    //获取全部菜单
    @GetMapping("/db/menu/selectList")
    List<SysMenu> selectList();

    //新增菜单
    @PostMapping("/db/menu/insert")
    int insert(@RequestBody SysMenu sysMenu);

    //更新菜单
    @PostMapping("/db/menu/update")
    int update(@RequestBody SysMenu sysMenu);

    //删除菜单
    @PostMapping("/db/menu/delete")
    int delete(@RequestBody List<String> menuIds);
}
