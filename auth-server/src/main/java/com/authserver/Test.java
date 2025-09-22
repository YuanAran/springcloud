package com.authserver;

import com.authserver.controller.AuthController;
import com.authserver.entity.LoginRequest;
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
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User("admin", encoder.encode("123456"));
        System.out.println(encoder.matches("123456", user.getPassword()));
        System.out.println("===========================================================");
        user.setPassword(encoder.encode("1234568"));
        System.out.println(encoder.matches("123456", user.getPassword()));
        System.out.println("===========================================================");
        System.out.println(encoder.matches("1234568", user.getPassword()));
    }
}
