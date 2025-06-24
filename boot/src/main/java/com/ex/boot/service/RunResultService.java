package com.ex.boot.service;

import com.ex.boot.mapper.RunResultMapper;
import com.ex.boot.model.Note;
import com.ex.boot.model.RunResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RunResultService {
    @Resource
    private RunResultMapper runResultMapper;

    public int addRunResult(RunResult runResult) {
        return runResultMapper.insertRunResult(runResult);
    }

    public int deleteById(Integer id) {
        return runResultMapper.deleteById(id);
    }

    public RunResult getRunResultById(Integer id) {
        return runResultMapper.selectById(id);
    }

    public List<RunResult> getRunResultsByNoteId(Integer noteId) {
        return runResultMapper.selectByNoteId(noteId);
    }
    public List<RunResult> getAllRunResults() {
        return runResultMapper.selectAll();
    }

    public List<RunResult> getAllRunResultsByUserId(Long userId) {
        return runResultMapper.selectByUserId(userId);
    }
}
