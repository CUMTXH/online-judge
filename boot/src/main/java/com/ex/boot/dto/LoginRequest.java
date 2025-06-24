// src/main/java/com/ex/boot/dto/LoginRequest.java
package com.ex.boot.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
