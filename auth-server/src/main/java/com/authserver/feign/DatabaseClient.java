package com.authserver.feign;

import com.authserver.entity.LoginRequest;
import com.commonentity.pojo.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("database-provider")
public interface DatabaseClient {
    @GetMapping("/db/selectByUname")
    LoginRequest selectByUname(@RequestParam("username") String username);
    @PostMapping("/db/insert")
    int insert(@RequestBody SysUser sysUser);
}
