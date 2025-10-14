package com.commonentity.pojo;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class SysDictItem {

  private String createBy;
  private Timestamp createTime;
  private String updateBy;
  private Timestamp updateTime;
  private String itemId;
  private String dictId;
  private String itemText;
  private String itemValue;
  private String description;
  private long sort;
  private String status;

}
