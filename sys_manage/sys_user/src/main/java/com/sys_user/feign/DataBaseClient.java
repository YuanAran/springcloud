package com.sys_user.feign;

import com.commonentity.pojo.SysUser;
import com.sys_user.entity.LoginRequest;
import com.sys_user.entity.UpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("database-provider")
public interface DataBaseClient {
    @PostMapping("/db/update")
    int update(@RequestBody UpdateRequest updateRequest);
    @GetMapping("/db/selectByUname")
    LoginRequest selectByUname(@RequestParam("username") String username);

    @PostMapping("/db/delete")
    int delete(@RequestBody List<String> usernames);

    @GetMapping("/db/selectList")
    List<SysUser> selectList();

    @PostMapping("/db/insert")
    int insert(SysUser sysUser);
}
