// src/main/java/com/ex/boot/controller/UserController.java
package com.ex.boot.controller;

import com.ex.boot.dto.ApiResponse;
import com.ex.boot.dto.LoginRequest;
import com.ex.boot.dto.RegisterRequest;
import com.ex.boot.service.UserService;
import com.ex.boot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        System.out.println(request);
        try {
            userService.register(request.getUsername(), request.getPassword());
            return ApiResponse.success(null, "注册成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(409, e.getMessage());
        }
    }

    @GetMapping("/test-register")
    public String testRegister(
            @RequestParam String username,
            @RequestParam String password
    ) {
        try {
            userService.register(username, password);
            return "注册成功: " + username;
        } catch (Exception e) {
            return "注册失败: " + e.getMessage();
        }
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {
        return userService.validateUser(request.getUsername(), request.getPassword());

    }

    @GetMapping("/auth/validate")
    public ApiResponse<Boolean> validateToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (JwtTokenUtil.validateToken(token)) {
                return ApiResponse.success(true);
            }
        }
        return ApiResponse.error(401, "无效的 token");
    }
}
