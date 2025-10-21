package com.commonentity.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("sys_dept")
public class SysDept {

  private String createBy;
  private Timestamp createTime;
  private String updateBy;
  private Timestamp updateTime;
  private String deptId;
  private String deptCode;
  private String parentId;
  private String deptName;
  private String deptEn;
  private String deptAbbr;
  private long deptSort;
  private String description;
  private String phone;
  private String fax;
  private String address;
  private String status;
  private String remark;

}
