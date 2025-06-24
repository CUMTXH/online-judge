package com.ex.boot.dto;

import lombok.Data;

// src/main/java/com/ex/boot/dto/ApiResponse.java
@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    // 修改 error 方法的泛型定义
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = code;
        response.message = message;
        response.data = null;
        return response;
    }

    // 保持 success 方法不变
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 200;
        response.message = "成功";
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 200;
        response.message = message;
        response.data = data;
        return response;
    }
}
