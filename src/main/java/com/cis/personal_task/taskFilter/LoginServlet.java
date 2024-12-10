package com.cis.personal_task.taskFilter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");  // 사용자 ID를 폼에서 받음
        String password = request.getParameter("password");  // 비밀번호 받음

        // 간단한 인증 (실제 애플리케이션에서는 더 복잡한 인증 로직 필요)
        if ("validUser".equals(userId) && "password".equals(password)) {
            HttpSession session = request.getSession(); // 세션 가져오기
            session.setAttribute("userId", userId);  // 세션에 사용자 ID 저장
            response.getWriter().write("Login successful");
        } else {
            response.getWriter().write("Invalid credentials");
        }
    }
}


