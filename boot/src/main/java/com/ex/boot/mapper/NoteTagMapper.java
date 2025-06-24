package com.ex.boot.mapper;

import com.ex.boot.model.NoteTag;
import com.ex.boot.model.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteTagMapper {
    void insertNoteTag(NoteTag noteTag);
    void insertNoteTags(@Param("noteId") Long noteId, @Param("tagIds") List<Long> tagIds);
    void deleteTagsByNoteId(Long noteId);
    void deleteTagsByNoteIdAndTagId(NoteTag noteTag);
    List<Tag> getTagsByNoteId(Long noteId);
//    void updateNoteTags(Long noteId, List<Long> tagIds);
}
