package com.authserver.feign;

import com.authserver.entity.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("database-provider")
public interface DatabaseClient {
    @GetMapping("/db/selectByUname")
    LoginRequest selectByUname(@RequestParam("username") String username);
    @GetMapping("/db/insert")
    int insert(@RequestParam("username") String username,@RequestParam("password") String password
            ,@RequestParam("sex") long sex,@RequestParam("nickname") String nickname);
}
