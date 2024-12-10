package com.cis.personal_task.taskFilter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class CustomAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 세션에서 사용자 정보 가져오기
        HttpSession session = httpRequest.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;

        if (userId == null) {
            // 헤더에서 토큰 가져오기
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !isValidToken(authHeader)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            // 인증 성공 시 세션에 사용자 정보 저장
            session = httpRequest.getSession(true); // 세션 생성
            session.setAttribute("userId", extractUserIdFromToken(authHeader));
        }

        chain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        // 토큰 검증 로직
        return "valid-token".equals(token);
    }

    private String extractUserIdFromToken(String token) {
        // 토큰에서 사용자 ID 추출 로직
        return "user1"; // 간단한 예로 사용자 ID 반환
    }
}
