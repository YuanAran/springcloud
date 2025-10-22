package com.databaseprovider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.commonentity.pojo.SysMenu;
import com.commonentity.pojo.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<SysRoleMenu> {
    @Select("select m.menu_id,m.menu_name,m.parent_id " +
            "from sys_menu m join sys_role_menu r on m.menu_id = r.menu_id " +
            "where r.role_id=#{roleId}")
    List<SysMenu> getRoleMenus(String roleId);
}
