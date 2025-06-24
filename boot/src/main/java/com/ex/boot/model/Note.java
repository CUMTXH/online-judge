package com.ex.boot.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Data
public class Note {
    private Long noteId;
    private Long userId;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private String type;
    private String input;
    private List<Long> tags;
}
