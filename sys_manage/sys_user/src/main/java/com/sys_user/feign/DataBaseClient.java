package com.sys_user.feign;

import com.commonentity.entity.LoginRequest;
import com.commonentity.entity.UpdateRequest;
import com.commonentity.pojo.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("database-provider")
public interface DataBaseClient {
    @PostMapping("/db/user/update")
    int update(@RequestBody UpdateRequest updateRequest);
    @GetMapping("/db/user/selectByUname")
    LoginRequest selectByUname(@RequestParam("username") String username);

    @PostMapping("/db/user/delete")
    int delete(@RequestBody List<String> usernames);

    @GetMapping("/db/user/selectList")
    List<SysUser> selectList();

    @PostMapping("/db/user/insert")
    int insert(SysUser sysUser);
}
