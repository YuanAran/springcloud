package com.authserver.feign;

import com.authserver.entity.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.POST;

@FeignClient("database-provider")
public interface DatabaseClient {
    @GetMapping("/db/selectByUname")
    LoginRequest selectByUname(@RequestParam("username") String username);
}
