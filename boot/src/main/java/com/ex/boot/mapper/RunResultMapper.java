package com.ex.boot.mapper;


import com.ex.boot.model.RunResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RunResultMapper {

    int insertRunResult(RunResult runResult);

    int deleteById(Integer id);

    RunResult selectById(Integer id);

    List<RunResult> selectByNoteId(Integer noteId);

    List<RunResult> selectAll();

    List<RunResult> selectByUserId(Long userId);
}
