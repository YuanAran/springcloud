package com.databaseprovider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.commonentity.pojo.SysRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {
}
