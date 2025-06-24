package com.ex.boot.mapper;

import com.ex.boot.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUsername(String username); // 方法名与XML中的id一致
    void insertUser(User user);          // 参数类型与XML中的parameterType一致
}
