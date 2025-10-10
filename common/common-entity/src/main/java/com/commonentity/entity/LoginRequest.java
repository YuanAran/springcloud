package com.commonentity.entity;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String username;
    private String password;
}
