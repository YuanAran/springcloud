package com.commonentity.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("sys_menu")
public class SysMenu {

  private String createBy;
  private Timestamp createTime;
  private String updateBy;
  private Timestamp updateTime;
  //非null 主键
  private String menuId;

  private String parentId;
  private String menuName;
  private String component;
  private String path;
  private String menuType;
  private String perms;
  private long sort;
  private String icon;
  private long leaf;
  private long hidden;
  private String description;
  private String status;
  private long internalOrExternal;


}
