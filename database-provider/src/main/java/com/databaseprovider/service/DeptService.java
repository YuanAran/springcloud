package com.databaseprovider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.commonentity.pojo.SysDept;
import com.databaseprovider.mapper.DeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeptService {
    @Autowired
    private DeptMapper deptMapper;

    //获取全部部门
    public List<SysDept> selectList(){
        QueryWrapper<SysDept> wrapper = new QueryWrapper<SysDept>()
                .select("dept_id","dept_code","dept_name","dept_sort","status");
        return deptMapper.selectList(wrapper);
    }

    //根据部门编号，名称，状态查询部门
    public List<SysDept> selectByDept(SysDept sysDept){
        QueryWrapper<SysDept> wrapper = new QueryWrapper<SysDept>()
                .select("dept_code","dept_name","dept_sort","status")
                .like(sysDept.getDeptCode() != null, "dept_code", sysDept.getDeptCode())
                .like(sysDept.getDeptName() != null, "dept_name", sysDept.getDeptName())
                .eq(sysDept.getStatus() != null, "status", sysDept.getStatus());
                return deptMapper.selectList(wrapper);
    }

    //新增部门
    public int insert(SysDept sysDept) {return deptMapper.insert(sysDept);}

    //删除部门
    public int delete(List<String> deptIds) {
        int totalDeleted = 0;
        for (String deptId : deptIds) {
            QueryWrapper<SysDept> wrapper = new QueryWrapper<SysDept>()
                    .eq("dept_id", deptId);
            totalDeleted += deptMapper.delete(wrapper);
        }
        return totalDeleted;
    }
}
