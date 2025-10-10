package com.sys_dept.feign;

import com.commonentity.pojo.SysDept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("database-provider")
public interface DatabaseClient {
    //获取全部部门
    @GetMapping("/db/dept/selectList")
     List<SysDept> selectList();

    //根据部门编号，名称，状态查询部门
    @PostMapping("/db/dept/selectByDept")
     List<SysDept> selectByDept(@RequestBody SysDept sysDept);

    //新增部门
    @PostMapping("/db/dept/insert")
     int insert(@RequestBody SysDept sysDept);

    //更新部门
    @PostMapping("/db/dept/update")
     int update(@RequestBody SysDept sysDept);

    //删除部门
    @PostMapping("/db/dept/delete")
     int delete(@RequestBody List<String> deptIds);
}
