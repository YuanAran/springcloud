package com.databaseprovider.controller;

import com.commonentity.pojo.SysDict;
import com.commonentity.pojo.SysDictItem;
import com.databaseprovider.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 字典的crud
@RestController
@RequestMapping("/db/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    // Dict
    @GetMapping("/selectList")
    public List<SysDict> selectDictList() {return dictService.selectDictList();}

    @PostMapping("/selectByDict")
    public List<SysDict> selectDictBy(@RequestBody SysDict sysDict) {return dictService.selectDictBy(sysDict);}    

    @PostMapping("/insert")
    public int insertDict(@RequestBody SysDict sysDict) {return dictService.insertDict(sysDict);}    

    @PostMapping("/update")
    public int updateDict(@RequestBody SysDict sysDict) {return dictService.updateDict(sysDict);}    

    @PostMapping("/delete")
    public int deleteDicts(@RequestBody List<String> dictIds) {return dictService.deleteDicts(dictIds);}    

    // DictItem
    @GetMapping("/item/selectByDictId")
    public List<SysDictItem> selectDictItems(@RequestParam("dictId") String dictId) {return dictService.selectDictItems(dictId);}    

    @PostMapping("/item/selectByItem")
    public List<SysDictItem> selectDictItemBy(@RequestBody SysDictItem item) {return dictService.selectDictItemBy(item);}    

    @PostMapping("/item/insert")
    public int insertDictItem(@RequestBody SysDictItem item) {return dictService.insertDictItem(item);}    

    @PostMapping("/item/update")
    public int updateDictItem(@RequestBody SysDictItem item) {return dictService.updateDictItem(item);}    

    @PostMapping("/item/delete")
    public int deleteDictItems(@RequestBody List<String> itemIds) {return dictService.deleteDictItems(itemIds);}    
}


