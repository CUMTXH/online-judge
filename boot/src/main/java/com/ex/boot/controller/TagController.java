package com.ex.boot.controller;

import com.ex.boot.model.Tag;
import com.ex.boot.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Resource
    TagService tagService;

    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    @PostMapping
    public ResponseEntity<Tag> addTag(@RequestBody Tag tag) {
        Tag savedTag = tagService.addTag(tag.getName());
        return ResponseEntity.ok(savedTag);
    }

}
