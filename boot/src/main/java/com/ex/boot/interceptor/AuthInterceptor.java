// src/main/java/com/ex/boot/interceptor/AuthInterceptor.java
package com.ex.boot.interceptor;

import com.ex.boot.util.JwtTokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("当前请求路径：" + request.getRequestURI());
        System.out.println("请求方法：" + request.getMethod());
        // 放行OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 排除登录注册接口
        if (request.getRequestURI().contains("/login") ||
                request.getRequestURI().contains("/register")) {
            return true;
        }

        // 获取Token
        String token = request.getHeader("Authorization");
        System.out.println("收到Token: "+token);
        if (token == null || !token.startsWith("Bearer ")) {
            sendError(response, 401, "未提供有效凭证");
            return false;
        }
        token = token.substring(7); // 去除Bearer前缀

        // 验证Token
        if (!JwtTokenUtil.validateToken(token)) {
            sendError(response, 401, "登录已过期，请重新登录");
            return false;
        }

        // 将用户名存入请求属性（后续Controller可直接使用）
        request.setAttribute("username", JwtTokenUtil.getUsernameFromToken(token));
        return true;
    }

    private void sendError(HttpServletResponse response, int code, String msg) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8"); // 明确指定编码
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + msg + "\"}");
    }
}
