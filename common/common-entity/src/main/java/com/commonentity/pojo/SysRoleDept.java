package com.commonentity.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_dept")
public class SysRoleDept {

  private String roleId;
  private String deptCode;

}
