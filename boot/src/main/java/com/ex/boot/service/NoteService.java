package com.ex.boot.service;

import com.ex.boot.model.Note;
import com.ex.boot.mapper.NoteMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class NoteService {

    @Resource
    private NoteMapper noteMapper;

    @Resource
    private TagService tagService;

//     * 根据用户ID获取该用户的所有笔记
    public List<Note> getNotesByUserId(Long userId) {
        return noteMapper.findByUserId(userId);
    }

//     * 添加一条新笔记
    public void addNote(Note note) {
        // 这里可以补充：校验 title/content 非空等
        noteMapper.insert(note);
    }

//     * 更新一条笔记
    public int updateNote(Note note) {
        int s = noteMapper.update(note);
        tagService.updateNoteTags(note.getNoteId(), note.getTags());
        return s;
    }

//     * 删除一条笔记
    public void deleteNote(Long noteId) {
        noteMapper.delete(noteId);
    }
}
