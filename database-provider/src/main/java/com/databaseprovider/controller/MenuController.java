package com.databaseprovider.controller;

import com.commonentity.pojo.SysMenu;
import com.databaseprovider.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//菜单的crud
@RestController
@RequestMapping("/db/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    //获取全部菜单
    @GetMapping("/selectList")
    public List<SysMenu> selectList() {
        return menuService.selectList();
    }

    //新增菜单
    @PostMapping("/insert")
    public int insert(@RequestBody SysMenu sysMenu) {
        UUID uuid = UUID.randomUUID();
        sysMenu.setMenuId(uuid.toString().replace("-",""));
        sysMenu.setCreateTime(new Timestamp(new Date().getTime()));
        return menuService.insert(sysMenu);
    }

    //更新菜单
    @PostMapping("/update")
    public int update(@RequestBody SysMenu sysMenu) {
        sysMenu.setUpdateTime(new Timestamp(new Date().getTime()));
        return menuService.update(sysMenu);
    }

    //删除菜单
    @PostMapping("/delete")
    public int delete(@RequestBody List<String> menuIds) {
        return menuService.delete(menuIds);
    }
}
