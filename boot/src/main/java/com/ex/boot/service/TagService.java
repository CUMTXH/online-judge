package com.ex.boot.service;

import com.ex.boot.mapper.NoteTagMapper;
import com.ex.boot.mapper.RunResultMapper;
import com.ex.boot.mapper.TagMapper;
import com.ex.boot.model.Tag;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Resource
    private TagMapper tagMapper;

    @Resource
    private NoteTagMapper noteTagMapper;

    public List<Tag> getAllTags() {
        return tagMapper.getAllTags();
    }
    public Tag addTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
        return tag; // 此时包含 ID
    }

    public List<Tag> getTagsByNoteId(Long noteId){
        return noteTagMapper.getTagsByNoteId(noteId);
    }

    public void updateNoteTags(Long noteId, List<Long> tagIds) {
        noteTagMapper.deleteTagsByNoteId(noteId);
        if (tagIds != null && !tagIds.isEmpty()) {
            noteTagMapper.insertNoteTags(noteId, tagIds);
        }
    }

}
