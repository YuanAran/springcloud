package com.databaseprovider.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@TableName("sys_user")
public class SysUser {
  @TableId(type = IdType.ASSIGN_ID)
  private String createBy;
  private Timestamp createTime;
  private String updateBy;
  private Timestamp updateTime;
  private String userId;
  private String username;
  private String nickname;
  private String password;
  private String avatar;
  private Date birthday;
  private long sex;
  private String email;
  private String phone;
  private String deptId;
  private String status;
  private String thirdId;
  private String thirdType;
  private String workNo;
  private String postIds;
}
