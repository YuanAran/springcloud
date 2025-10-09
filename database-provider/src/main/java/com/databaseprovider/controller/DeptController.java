package com.databaseprovider.controller;

import com.commonentity.pojo.SysDept;
import com.databaseprovider.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//部门的crud
@RestController
@RequestMapping("/db/dept")
public class DeptController {
    @Autowired
    private DeptService deptService;

    //获取全部部门
    @GetMapping("/selectList")
    public List<SysDept> selectList() {
        return deptService.selectList();
    }

    //根据部门编号，名称，状态查询部门
    @PostMapping("/selectByDept")
    public List<SysDept> selectByDept(@RequestBody SysDept sysDept) {
        return deptService.selectByDept(sysDept);
    }

    //新增部门
    @PostMapping("/insert")
    public int insert(@RequestBody SysDept sysDept) {
        return deptService.insert(sysDept);
    }

    //删除部门
    @PostMapping("/delete")
    public int delete(@RequestBody List<String> deptIds) {
        return deptService.delete(deptIds);
    }
}
