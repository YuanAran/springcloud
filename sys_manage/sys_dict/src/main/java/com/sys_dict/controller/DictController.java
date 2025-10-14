package com.sys_dict.controller;

import com.commonentity.pojo.SysDict;
import com.commonentity.pojo.SysDictItem;
import com.sys_dict.feign.DatabaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sys_dict")
public class DictController {
    @Autowired
    private DatabaseClient databaseClient;

    // Dict
    @GetMapping("/selectList")
    public List<SysDict> selectDictList(){return databaseClient.selectDictList();}

    @PostMapping("/selectByDict")
    public List<SysDict> selectDictBy(@RequestBody SysDict sysDict){return databaseClient.selectDictBy(sysDict);}    

    @PostMapping("/insert")
    public int insert(@RequestBody SysDict sysDict){
        UUID uuid=UUID.randomUUID();
        sysDict.setDictId(uuid.toString().replace("-",""));
        sysDict.setCreateTime(new Timestamp(new Date().getTime()));
        return databaseClient.insertDict(sysDict);
    }

    @PostMapping("/update")
    public int update(@RequestBody SysDict sysDict){
        sysDict.setUpdateTime(new Timestamp(new Date().getTime()));
        return databaseClient.updateDict(sysDict);
    }

    @PostMapping("/delete")
    public int delete(@RequestBody List<String> dictIds){return databaseClient.deleteDicts(dictIds);}    

    // DictItem
    @GetMapping("/item/selectByDictId")
    public List<SysDictItem> selectDictItems(@RequestParam("dictId") String dictId){return databaseClient.selectDictItems(dictId);}    

    @PostMapping("/item/selectByItem")
    public List<SysDictItem> selectDictItemBy(@RequestBody SysDictItem item){return databaseClient.selectDictItemBy(item);}    

    @PostMapping("/item/insert")
    public int insertItem(@RequestBody SysDictItem item){
        UUID uuid=UUID.randomUUID();
        item.setItemId(uuid.toString().replace("-",""));
        item.setCreateTime(new Timestamp(new Date().getTime()));
        return databaseClient.insertDictItem(item);
    }

    @PostMapping("/item/update")
    public int updateItem(@RequestBody SysDictItem item){
        item.setUpdateTime(new Timestamp(new Date().getTime()));
        return databaseClient.updateDictItem(item);
    }

    @PostMapping("/item/delete")
    public int deleteItems(@RequestBody List<String> itemIds){return databaseClient.deleteDictItems(itemIds);}    
}


