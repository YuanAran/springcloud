package com.databaseprovider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.commonentity.pojo.SysDict;
import com.commonentity.pojo.SysDictItem;
import com.databaseprovider.mapper.DictItemMapper;
import com.databaseprovider.mapper.DictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictService {
    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictItemMapper dictItemMapper;

    // Dict CRUD
    public List<SysDict> selectDictList() {
        QueryWrapper<SysDict> wrapper = new QueryWrapper<SysDict>()
                .select("dict_id","dict_name","dict_code","status");
        return dictMapper.selectList(wrapper);
    }

    public List<SysDict> selectDictBy(SysDict sysDict) {
        QueryWrapper<SysDict> wrapper = new QueryWrapper<SysDict>()
                .select("dict_id","dict_name","dict_code","status")
                .like(sysDict.getDictName() != null, "dict_name", sysDict.getDictName())
                .like(sysDict.getDictCode() != null, "dict_code", sysDict.getDictCode())
                .eq(sysDict.getStatus() != null, "status", sysDict.getStatus());
        return dictMapper.selectList(wrapper);
    }

    public int insertDict(SysDict sysDict) {return dictMapper.insert(sysDict);}    

    public int updateDict(SysDict sysDict) {
        QueryWrapper<SysDict> wrapper = new QueryWrapper<SysDict>()
                .eq("dict_id", sysDict.getDictId());
        return dictMapper.update(sysDict, wrapper);
    }

    public int deleteDicts(List<String> dictIds) {
        int totalDeleted = 0;
        for (String dictId : dictIds) {
            QueryWrapper<SysDict> wrapper = new QueryWrapper<SysDict>()
                    .eq("dict_id", dictId);
            totalDeleted += dictMapper.delete(wrapper);
        }
        return totalDeleted;
    }

    // DictItem CRUD
    public List<SysDictItem> selectDictItems(String dictId) {
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<SysDictItem>()
                .select("item_id","dict_id","item_text","item_value","sort","status")
                .eq("dict_id", dictId);
        return dictItemMapper.selectList(wrapper);
    }

    public List<SysDictItem> selectDictItemBy(SysDictItem sysDictItem) {
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<SysDictItem>()
                .select("item_id","dict_id","item_text","item_value","sort","status")
                .eq("dict_id", sysDictItem.getDictId());
        return dictItemMapper.selectList(wrapper);
    }

    public int insertDictItem(SysDictItem item) {return dictItemMapper.insert(item);}    

    public int updateDictItem(SysDictItem item) {
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<SysDictItem>()
                .eq("item_id", item.getItemId());
        return dictItemMapper.update(item, wrapper);
    }

    public int deleteDictItems(List<String> itemIds) {
        int totalDeleted = 0;
        for (String itemId : itemIds) {
            QueryWrapper<SysDictItem> wrapper = new QueryWrapper<SysDictItem>()
                    .eq("item_id", itemId);
            totalDeleted += dictItemMapper.delete(wrapper);
        }
        return totalDeleted;
    }
}


