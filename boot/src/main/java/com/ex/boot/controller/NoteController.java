package com.ex.boot.controller;

import com.ex.boot.dto.ApiResponse;
import com.ex.boot.dto.RunCodeRequest;
import com.ex.boot.model.Note;
import com.ex.boot.model.RunResult;
import com.ex.boot.model.Tag;
import com.ex.boot.service.NoteService;
import com.ex.boot.service.RunCodeService;
import com.ex.boot.util.JwtTokenUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Resource
    private NoteService noteService;

    @Resource
    private RunCodeService RunCodeService;

    // 获取当前用户所有笔记
    @GetMapping
    public ApiResponse<List<Note>> list(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = JwtTokenUtil.extractUserId(token);
        List<Note> notes = noteService.getNotesByUserId(userId);
        return ApiResponse.success(notes);
    }

    // 添加新笔记
    @PostMapping("/add")
    public ApiResponse<Void> add(HttpServletRequest request, @RequestBody Note note) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = JwtTokenUtil.extractUserId(token);
        note.setUserId(userId);
        System.out.println(note);
        noteService.addNote(note);
        return ApiResponse.success(null, "笔记添加成功");
    }

    // 更新笔记
    @PutMapping("/{noteId}")
    public ApiResponse<Void> updateNote(
            @PathVariable Long noteId,
            @RequestBody Note note,
            HttpServletRequest request
    ) {
        // 提取 token 中的 userId
        String token = request.getHeader("Authorization").substring(7);
        Long userId = JwtTokenUtil.extractUserId(token);

        // 手动设置 noteId 和 userId，确保匹配
        note.setNoteId(noteId);
        note.setUserId(userId);

        int result = noteService.updateNote(note);
        if (result > 0) {
            return ApiResponse.success(null, "笔记更新成功");
        } else {
            return ApiResponse.error(401, "笔记不存在或无权限");
        }
    }

    // 删除笔记
    @DeleteMapping("/{noteId}")
    public ApiResponse<Void> delete(HttpServletRequest request, @PathVariable Long noteId) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = JwtTokenUtil.extractUserId(token);

        noteService.deleteNote(noteId);
        return ApiResponse.success(null, "笔记删除成功");
    }

    @PutMapping("/run/{noteId}")
    public ApiResponse<Map<String, String>> updateAndRun(
            @PathVariable Long noteId,
            @RequestBody Note note,
            HttpServletRequest httpRequest
    ) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        Long userId = JwtTokenUtil.extractUserId(token);

        note.setNoteId(noteId);
        note.setUserId(userId);

        int result = noteService.updateNote(note);
        if (result <= 0) {
            return ApiResponse.error(401, "笔记不存在或无权限");
        }

        RunResult output = RunCodeService.runCode(note);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data = mapper.convertValue(output, new TypeReference<Map<String, String>>() {});
        System.out.println(data);
        return ApiResponse.success(data, "运行成功");

    }


//    @PutMapping("/{id}/tags")
//    public void updateNoteTags(@PathVariable Long id, @RequestBody List<Long> tagIds) {
//        noteService.updateNoteTags(id, tagIds);
//    }
//
//    @GetMapping("/{id}/tags")
//    public List<Tag> getNoteTags(@PathVariable Long id) {
//        return noteService.getTagsByNoteId(id);
//    }

}
