package com.ex.boot.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RunCodeRequest {
    // Getter / Setter
    private Long noteId;
    private String title;
    private String type;
    private String content;
    private String input;

}

