package com.authserver;

import com.authserver.until.JwtUntil;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Test {

    @Data
    static class User {
        private String username;
        private String password;
        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
    public static void main(String[] args) {
        System.out.println(JwtUntil.generateToken("yuan"));
    }
}
