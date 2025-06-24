package com.ex.boot.controller;

import com.ex.boot.dto.ApiResponse;
import com.ex.boot.model.Note;
import com.ex.boot.model.RunResult;
import com.ex.boot.service.RunResultService;
import com.ex.boot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.List;

@RestController
@RequestMapping("/api/runresults")
public class RunResultController {

    @Resource
    private RunResultService runResultService;

    // 新增运行结果
    @PostMapping("/add")
    public RunResult addRunResult(@RequestBody RunResult runResult) {
        System.out.println(runResult);
        runResultService.addRunResult(runResult);
        return runResult;
    }

    @GetMapping
    public List<RunResult> getRunResultsByUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = JwtTokenUtil.extractUserId(token);
        List<RunResult> results = runResultService.getAllRunResultsByUserId(userId);
        return results;
    }
    // 根据ID查询
    @GetMapping("/{id}")
    public RunResult getRunResultById(@PathVariable Integer id) {
        return runResultService.getRunResultById(id);
    }

    // 根据笔记ID查询所有运行结果
    @GetMapping("/note/{noteId}")
    public List<RunResult> getRunResultsByNoteId(@PathVariable Integer noteId) {
        return runResultService.getRunResultsByNoteId(noteId);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable Integer id) {
        if (runResultService.deleteById(id) >0) {
            return  ApiResponse.success(null, "笔记删除成功");
        } else {
            return  ApiResponse.error(400, "删除失败");
        }
    }

}
