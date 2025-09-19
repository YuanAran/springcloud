package com.sys_user.entity;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class UpdateRequest {
    private String createBy;
    private String updateBy;
    private Timestamp updateTime;
    private String userId;
    private String username;
    private String nickname;
    private String password;
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
