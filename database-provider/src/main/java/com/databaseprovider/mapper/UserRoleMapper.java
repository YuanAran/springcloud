package com.databaseprovider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.commonentity.pojo.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<SysUserRole> {
}
