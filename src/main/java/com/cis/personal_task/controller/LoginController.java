package com.cis.personal_task.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestParam String userId, @RequestParam String password, HttpSession session) {
        // 인증 로직 (간단한 예시)
        if ("validUser".equals(userId) && "password".equals(password)) {
            session.setAttribute("userId", userId);  // 세션에 사용자 ID 저장
            return "Login successful";
        } else {
            return "Invalid credentials";
        }
    }
}
