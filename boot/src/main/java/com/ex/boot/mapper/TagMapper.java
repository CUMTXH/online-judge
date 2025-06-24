package com.ex.boot.mapper;

import com.ex.boot.model.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    List<Tag> getAllTags();
    int insert(Tag tag);
}
