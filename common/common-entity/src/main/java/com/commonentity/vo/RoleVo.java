package com.commonentity.vo;

import com.commonentity.pojo.SysDept;
import com.commonentity.pojo.SysMenu;
import lombok.Data;

import java.util.List;

/**
 * role，dept，menu，user的聚合查询数据
 **/
@Data
public class RoleVo {
    private String roleId;
    private String roleName;
    private List<SysDept> depts;
    private List<SysMenu> menus;
}
