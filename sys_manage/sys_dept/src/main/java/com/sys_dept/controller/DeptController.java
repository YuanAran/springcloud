package com.sys_dept.controller;

import com.commonentity.pojo.SysDept;
import com.sys_dept.feign.DatabaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sys_dept")
public class DeptController {
    @Autowired
    private DatabaseClient databaseClient;
    @GetMapping("/getAllDept")
    public List<SysDept> getAllDept(){
        return databaseClient.selectList();
    }
    @PostMapping("/getDepts")
    public List<SysDept> getDeptByIds(@RequestBody SysDept sysDept){
        return databaseClient.selectByDept(sysDept);
    }
    @PostMapping("/addDept")
    public int addDept(@RequestBody SysDept sysDept){
        UUID uuid=UUID.randomUUID();
        sysDept.setDeptId(uuid.toString().replace("-",""));
        sysDept.setCreateTime(new Timestamp(new Date().getTime()));
        return databaseClient.insert(sysDept);
    }
    @PostMapping("/updateDept")
    public int updateDept(@RequestBody SysDept sysDept){
        sysDept.setUpdateTime(new Timestamp(new Date().getTime()));
        return databaseClient.update(sysDept);
    }
    @PostMapping("/deleteDept")
    public int deleteDept(@RequestBody List<String> deptIds){
        return databaseClient.delete(deptIds);
    }
}
