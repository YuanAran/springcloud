package com.databaseprovider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.commonentity.pojo.SysMenu;
import com.databaseprovider.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    @Autowired
    private MenuMapper menuMapper;

    //获取全部菜单
    public List<SysMenu> selectList(){
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<SysMenu>()
                .select("menu_id","parent_id","menu_name","component","path","menu_type","perms","sort","hidden","status");
        return menuMapper.selectList(wrapper);
    }

    //新增菜单
    public int insert(SysMenu sysMenu) {return menuMapper.insert(sysMenu);}
    
    //更新菜单
    public int update(SysMenu sysMenu){
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<SysMenu>()
                .eq("menu_id", sysMenu.getMenuId());
        return menuMapper.update(sysMenu,wrapper);
    }

    //删除菜单
    public int delete(List<String> menuIds) {
        int totalDeleted = 0;
        for (String menuId : menuIds) {
            QueryWrapper<SysMenu> wrapper = new QueryWrapper<SysMenu>()
                    .eq("menu_id", menuId);
            totalDeleted += menuMapper.delete(wrapper);
        }
        return totalDeleted;
    }
}
