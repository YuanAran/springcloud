package com.commonentity.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("sys_role")
public class SysRole {

  private String createBy;
  private Timestamp createTime;
  private String updateBy;
  private Timestamp updateTime;
  private String roleId;
  private String roleName;
  private String roleCode;
  private String description;
  private String status;
  private String dataScope;
  private String deptIds;

}
