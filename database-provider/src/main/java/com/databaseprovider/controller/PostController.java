package com.databaseprovider.controller;

import com.commonentity.pojo.SysPost;
import com.databaseprovider.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//岗位的crud
@RestController
@RequestMapping("/db/post")
public class PostController {
    @Autowired
    private PostService postService;

    //获取全部岗位
    @GetMapping("/selectList")
    public List<SysPost> selectList() {
        return postService.selectList();
    }

    //根据岗位编号，名称查询岗位
    @PostMapping("/selectByPost")
    public List<SysPost> selectByPost(@RequestBody SysPost sysPost) {
        return postService.selectByPost(sysPost);
    }

    //新增岗位
    @PostMapping("/insert")
    public int insert(@RequestBody SysPost sysPost) {
        return postService.insert(sysPost);
    }

    //更新岗位
    @PostMapping("/update")
    public int update(@RequestBody SysPost sysPost) {
        return postService.update(sysPost);
    }

    //删除岗位
    @PostMapping("/delete")
    public int delete(@RequestBody List<String> postIds) {
        return postService.delete(postIds);
    }
}
