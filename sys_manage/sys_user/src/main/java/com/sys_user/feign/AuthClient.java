package com.sys_user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("auth-server")
public interface AuthClient {
    @GetMapping("/auth/getUsername")
    String getUsername(@RequestHeader("Authorization") String token);
}
