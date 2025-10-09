package com.databaseprovider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.commonentity.pojo.SysDept;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeptMapper extends BaseMapper<SysDept> {
}
