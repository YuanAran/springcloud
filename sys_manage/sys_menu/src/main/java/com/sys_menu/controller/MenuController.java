package com.sys_menu.controller;

import com.commonentity.pojo.SysMenu;
import com.sys_menu.feign.DatabaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sys_menu")
public class MenuController {
    @Autowired
    private DatabaseClient databaseClient;

    @GetMapping("/getAllMenu")
    public List<SysMenu> getAllMenu(){
        return databaseClient.selectList();
    }

    @PostMapping("/addMenu")
    public int addMenu(@RequestBody SysMenu sysMenu){
        UUID uuid = UUID.randomUUID();
        sysMenu.setMenuId(uuid.toString().replace("-",""));
        sysMenu.setCreateTime(new Timestamp(new Date().getTime()));
        return databaseClient.insert(sysMenu);
    }

    @PostMapping("/updateMenu")
    public int updateMenu(@RequestBody SysMenu sysMenu){
        sysMenu.setUpdateTime(new Timestamp(new Date().getTime()));
        return databaseClient.update(sysMenu);
    }

    @PostMapping("/deleteMenu")
    public int deleteMenu(@RequestBody List<String> menuIds){
        return databaseClient.delete(menuIds);
    }
}
