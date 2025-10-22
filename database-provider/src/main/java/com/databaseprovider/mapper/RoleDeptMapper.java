package com.databaseprovider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.commonentity.pojo.SysDept;
import com.commonentity.pojo.SysRoleDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleDeptMapper extends BaseMapper<SysRoleDept> {
    @Select("select d.dept_id,d.dept_name,d.dept_code " +
            "from sys_dept d join sys_role_dept r on d.dept_code = r.dept_code " +
            "where r.role_id=#{roleId} ")
    List<SysDept> getRoleDepts(@Param("roleId") String roleId);
}
