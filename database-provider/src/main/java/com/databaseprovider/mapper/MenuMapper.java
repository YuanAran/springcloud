package com.databaseprovider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.commonentity.pojo.SysMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuMapper extends BaseMapper<SysMenu> {
}
