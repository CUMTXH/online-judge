// src/test/java/com/ex/boot/test/UserServiceTest.java
package com.ex.boot.Test;

import com.ex.boot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // 必须添加此注解
public class UserServiceTest {

    @Autowired // 必须添加自动注入注解
    private UserService userService;

    @org.junit.Test
    @Test
    public void testRegister() {
        userService.register("testuser", "mypassword123");
    }
}
