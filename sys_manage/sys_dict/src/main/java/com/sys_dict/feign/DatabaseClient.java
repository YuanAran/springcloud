package com.sys_dict.feign;

import com.commonentity.pojo.SysDict;
import com.commonentity.pojo.SysDictItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("database-provider")
public interface DatabaseClient {
    // Dict
    @GetMapping("/db/dict/selectList")
    List<SysDict> selectDictList();

    @PostMapping("/db/dict/selectByDict")
    List<SysDict> selectDictBy(@RequestBody SysDict sysDict);

    @PostMapping("/db/dict/insert")
    int insertDict(@RequestBody SysDict sysDict);

    @PostMapping("/db/dict/update")
    int updateDict(@RequestBody SysDict sysDict);

    @PostMapping("/db/dict/delete")
    int deleteDicts(@RequestBody List<String> dictIds);

    // DictItem
    @GetMapping("/db/dict/item/selectByDictId")
    List<SysDictItem> selectDictItems(@RequestParam("dictId") String dictId);

    @PostMapping("/db/dict/item/selectByItem")
    List<SysDictItem> selectDictItemBy(@RequestBody SysDictItem item);

    @PostMapping("/db/dict/item/insert")
    int insertDictItem(@RequestBody SysDictItem item);

    @PostMapping("/db/dict/item/update")
    int updateDictItem(@RequestBody SysDictItem item);

    @PostMapping("/db/dict/item/delete")
    int deleteDictItems(@RequestBody List<String> itemIds);
}


