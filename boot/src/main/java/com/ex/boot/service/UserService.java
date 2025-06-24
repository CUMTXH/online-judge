// src/main/java/com/ex/boot/service/UserService.java
package com.ex.boot.service;

import com.ex.boot.dto.ApiResponse;
import com.ex.boot.mapper.UserMapper;
import com.ex.boot.model.User;
import com.ex.boot.util.JwtTokenUtil;
import com.ex.boot.util.MD5Util;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void register(String username, String password) {
        if (userMapper.findByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Util.encrypt(password));
        user.setCreatedAt(new Date());

        userMapper.insertUser(user);
    }


    public ApiResponse<String> validateUser(String username, String password) {
        User user = userMapper.findByUsername(username);
        String encryptedPassword = MD5Util.encrypt(password);
        if (user == null || !encryptedPassword.equals(user.getPassword())) {
            return ApiResponse.error(401, "用户名或密码错误");
        }
        String token = JwtTokenUtil.generateToken(user.getUsername(), user.getUserId()); // 使用新方法生成 token
        return ApiResponse.success(token);
    }
}
