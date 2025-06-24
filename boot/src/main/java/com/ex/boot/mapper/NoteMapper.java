package com.ex.boot.mapper;

import com.ex.boot.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {
    List<Note> findByUserId(Long userId);
    void insert(Note note);
    int update(Note note);
    void delete(Long noteId);
    List<Long> findTagIdsByNoteId(Long noteId);
}
