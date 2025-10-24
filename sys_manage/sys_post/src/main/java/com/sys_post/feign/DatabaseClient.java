package com.sys_post.feign;

import com.commonentity.pojo.SysPost;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("database-provider")
public interface DatabaseClient {
    //获取全部岗位
    @GetMapping("/db/post/selectList")
    List<SysPost> selectList();

    //根据岗位编号，名称查询岗位
    @PostMapping("/db/post/selectByPost")
    List<SysPost> selectByPost(@RequestBody SysPost sysPost);

    //新增岗位
    @PostMapping("/db/post/insert")
    int insert(@RequestBody SysPost sysPost);

    //更新岗位
    @PostMapping("/db/post/update")
    int update(@RequestBody SysPost sysPost);

    //删除岗位
    @PostMapping("/db/post/delete")
    int delete(@RequestBody List<String> postIds);
}
