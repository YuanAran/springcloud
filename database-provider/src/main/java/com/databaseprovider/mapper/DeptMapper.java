package com.databaseprovider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.commonentity.pojo.SysDept;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeptMapper extends BaseMapper<SysDept> {
}
