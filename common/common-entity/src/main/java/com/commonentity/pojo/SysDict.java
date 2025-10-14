package com.commonentity.pojo;

import lombok.Data;

import java.sql.Timestamp;


/**
 * sys_dict表与sys_dict_item表的关系是一对多，后者是前者字典的具体值
 */
@Data
public class SysDict {

  private String createBy;
  private Timestamp createTime;
  private String updateBy;
  private Timestamp updateTime;
  private String dictId;
  private String dictName;
  private String dictCode;
  private String description;
  private String status;
}
