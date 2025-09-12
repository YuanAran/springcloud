package com.authserver.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import javax.ws.rs.POST;

@FeignClient("database-provider")
public interface DatabaseClient {
    @GetMapping("/db/selectByUname")
    String selectByUname();
}
