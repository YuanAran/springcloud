package com.databaseprovider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.commonentity.pojo.SysPost;
import com.databaseprovider.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostMapper postMapper;

    //获取全部岗位
    public List<SysPost> selectList(){
        QueryWrapper<SysPost> wrapper = new QueryWrapper<SysPost>()
                .select("post_id","post_code","post_name","sort","create_time");
        return postMapper.selectList(wrapper);
    }

    //根据岗位编号，名称查询岗位
    public List<SysPost> selectByPost(SysPost sysPost){
        QueryWrapper<SysPost> wrapper = new QueryWrapper<SysPost>()
                .select("post_id","post_code","post_name","sort","create_time")
                .like(sysPost.getPostCode() != null, "post_code", sysPost.getPostCode())
                .like(sysPost.getPostName() != null, "post_name", sysPost.getPostName());
        return postMapper.selectList(wrapper);
    }

    //新增岗位
    public int insert(SysPost sysPost) {
        return postMapper.insert(sysPost);
    }

    //更新岗位
    public int update(SysPost sysPost){
        QueryWrapper<SysPost> wrapper = new QueryWrapper<SysPost>()
                .eq("post_id", sysPost.getPostId());
        return postMapper.update(sysPost, wrapper);
    }

    //删除岗位
    public int delete(List<String> postIds) {
        int totalDeleted = 0;
        for (String postId : postIds) {
            QueryWrapper<SysPost> wrapper = new QueryWrapper<SysPost>()
                    .eq("post_id", postId);
            totalDeleted += postMapper.delete(wrapper);
        }
        return totalDeleted;
    }
}
