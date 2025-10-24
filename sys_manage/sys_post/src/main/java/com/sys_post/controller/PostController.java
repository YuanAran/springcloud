package com.sys_post.controller;

import com.commonentity.pojo.SysPost;
import com.sys_post.feign.DatabaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sys_post")
public class PostController {
    @Autowired
    private DatabaseClient databaseClient;

    @GetMapping("/getAllPost")
    public List<SysPost> getAllPost(){
        return databaseClient.selectList();
    }

    @PostMapping("/getPosts")
    public List<SysPost> getPostByIds(@RequestBody SysPost sysPost){
        return databaseClient.selectByPost(sysPost);
    }

    @PostMapping("/addPost")
    public int addPost(@RequestBody SysPost sysPost){
        UUID uuid = UUID.randomUUID();
        sysPost.setPostId(uuid.toString().replace("-",""));
        sysPost.setCreateTime(new Timestamp(new Date().getTime()));
        return databaseClient.insert(sysPost);
    }

    @PostMapping("/updatePost")
    public int updatePost(@RequestBody SysPost sysPost){
        sysPost.setUpdateTime(new Timestamp(new Date().getTime()));
        return databaseClient.update(sysPost);
    }

    @PostMapping("/deletePost")
    public int deletePost(@RequestBody List<String> postIds){
        return databaseClient.delete(postIds);
    }
}
