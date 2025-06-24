package com.ex.boot.service;

import com.ex.boot.model.Note;
import com.ex.boot.model.RunResult;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RunCodeService {
    private static final String JUDGE_URL = "http://localhost:5050/run";

    private final RestTemplate restTemplate = new RestTemplate();

    public RunResult runCode(Note note) {
        String type = note.getType().toLowerCase();

        return switch (type) {
            case "c", "c++", "java" -> compileAndRun(note);
            case "python", "javascript" -> runScript(note);
            default -> throw new UnsupportedOperationException("暂不支持该语言：" + type);
        };
    }

    public RunResult compileAndRun(Note note) {

        String type = note.getType().toLowerCase();
        String code = note.getContent();
        String input = note.getInput();
        String filename = "";
        String arg = "";
        String exefile = "";
        List args = null;
        List exeargs = null;
        Long memLim = 104857600L;
        switch (type) {
            case "c":
                filename = "a.c";
                arg = "/usr/bin/gcc";
                exefile = "a";
                args = List.of(arg, filename, "-o", exefile);
                exeargs = List.of(exefile);
                break;
            case "c++":
                filename = "a.cc";
                arg = "/usr/bin/g++";
                exefile = "a";
                args = List.of(arg, filename, "-o", exefile);
                exeargs = List.of(exefile);
                break;
            case "java":
                filename = "Main.java";
                arg = "/usr/bin/javac";
                exefile = "Main.class";
                args = List.of(arg, filename);
                exeargs = List.of("/usr/bin/java", "Main");
                memLim = 268435456L;
                break;
            default:
                throw new UnsupportedOperationException("暂不支持该语言：" + type);
        }

        Map<String, Object> compileRequest = new HashMap<>();
        compileRequest.put("cmd", List.of(Map.of(
                "args", args,
                "env", List.of("PATH=/usr/bin:/bin"),
                "files", List.of(
                        Map.of("content", ""),
                        Map.of("name", "stdout", "max", 10240),
                        Map.of("name", "stderr", "max", 10240)
                ),
                "cpuLimit", 5000000000L,
//                "clockLimit",10000000000L,
                "memoryLimit", memLim,
                "procLimit", 50,
                "copyIn", Map.of(filename, Map.of("content", code)),
                "copyOut", List.of("stdout", "stderr"),
                "copyOutCached", List.of(exefile)
        )));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(compileRequest, headers);

        ResponseEntity<List> compileResponse = restTemplate.exchange(
                JUDGE_URL,
                HttpMethod.POST,
                request,
                List.class
        );
        if (!compileResponse.getStatusCode().is2xxSuccessful()) {
            return new RunResult("", "评测机返回异常状态：" + compileResponse.getStatusCode(), "Error", -1, 0, 0, 0);
        }

        Map<String, Object> compileResult = (Map<String, Object>) compileResponse.getBody().get(0);

        if (!"Accepted".equals(compileResult.get("status")) || ((Number) compileResult.get("exitStatus")).intValue() != 0) {
            // 编译失败，直接返回stdout+stderr信息
            Map<String, String> files = (Map<String, String>) compileResult.get("files");
            String status = (String) compileResult.get("status");
            Integer exitStatus = (Integer) compileResult.get("exitStatus");
            Long time = compileResult.get("time") != null ? Long.parseLong(compileResult.get("time").toString()) : null;
            Long memory = compileResult.get("memory") != null ? Long.parseLong(compileResult.get("memory").toString()) : null;
            Long runTime = compileResult.get("runTime") != null ? Long.parseLong(compileResult.get("runTime").toString()) : null;

            Map<String, String> filesOut = (Map<String, String>) compileResult.get("files");

            String stdout = filesOut.getOrDefault("stdout", "").trim();
            String stderr = filesOut.getOrDefault("stderr", "").trim();

            return new RunResult("Compile Error:" + stdout, stderr, status, exitStatus, time, runTime, memory);
        }

        Map<String, String> fileIds = (Map<String, String>) compileResult.get("fileIds");
        String fileId = fileIds.get(exefile);

        Map<String, Object> runRequest = new HashMap<>();

        runRequest.put("cmd", List.of(Map.of(
                "args", exeargs,
                "env", List.of("PATH=/usr/bin:/bin"),
                "files", List.of(
                        Map.of("content", input == null ? "" : input),
                        Map.of("name", "stdout", "max", 10240),
                        Map.of("name", "stderr", "max", 10240)
                ),
                "cpuLimit", 5000000000L,
                "clockLimit",10000000000L,
                "memoryLimit", memLim,
                "procLimit", 50,
                "copyIn", Map.of(exefile, Map.of("fileId", fileId)),
                "copyOut", List.of("stdout", "stderr")
        )));
        request = new HttpEntity<>(runRequest, headers);
        ResponseEntity<List> runResponse = restTemplate.exchange(
                JUDGE_URL,
                HttpMethod.POST,
                request,
                List.class
        );
        if (!runResponse.getStatusCode().is2xxSuccessful()) {
            return new RunResult("", "评测机返回异常状态：" + runResponse.getStatusCode(), "Error", -1, 0, 0, 0);
        }

        Map<String, Object> runResult = (Map<String, Object>) runResponse.getBody().get(0);
        Map<String, String> runFiles = (Map<String, String>) runResult.get("files");

        System.out.println(runResult);
        System.out.println(runFiles);
        return new RunResult(
                runFiles.getOrDefault("stdout","").trim(),
                runFiles.getOrDefault("stderr","").trim(),
                (String) runResult.get("status"),
                (Integer) runResult.get("exitStatus"),
                runResult.get("time")!= null ? Long.parseLong(runResult.get("time").toString()) : null,
                runResult.get("runTime")!= null ? Long.parseLong(runResult.get("runTime").toString()) : null,
                runResult.get("memory")!= null ? Long.parseLong(runResult.get("memory").toString()) : null
        );
    }


    public RunResult runScript(Note note) {

        String type = note.getType().toLowerCase();
        String code = note.getContent();
        String input = note.getInput();
        String filename = "";
        String args = "";
        switch (type) {
            case "python":
                filename = "main.py";
                args = "/usr/bin/python3";
                break;
            case "javascript":
            case "js":
                filename = "main.js";
                args = "/usr/bin/node";
                break;
        }

        Map<String, Object> cmd = new HashMap<>();
        cmd.put("args", List.of(args, filename));
        cmd.put("env", List.of("PATH=/usr/bin:/bin"));
        cmd.put("files", List.of(
                Map.of("content", input != null ? input : ""), // stdin
                Map.of("name", "stdout", "max", 10240),
                Map.of("name", "stderr", "max", 10240)
        ));
        cmd.put("cpuLimit", 5000000000L);
        cmd.put("clockLimit", 10000000000L);
        cmd.put("memoryLimit", 134217728);
        cmd.put("procLimit", 50);

        Map<String, Object> copyIn = new HashMap<>();
        copyIn.put(filename, Map.of("content", code));
        cmd.put("copyIn", copyIn);
        cmd.put("copyOut", List.of("stdout", "stderr"));

        Map<String, Object> body = Map.of("cmd", List.of(cmd));

        // 2. 发送 POST 请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    JUDGE_URL,
                    HttpMethod.POST,
                    request,
                    List.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                // 获取返回的第一个评测结果
                Map<String, Object> result = (Map<String, Object>) response.getBody().get(0);

                String status = (String) result.get("status");
                Integer exitStatus = (Integer) result.get("exitStatus");
                Long time = result.get("time") != null ? Long.parseLong(result.get("time").toString()) : null;
                Long memory = result.get("memory") != null ? Long.parseLong(result.get("memory").toString()) : null;
                Long runTime = result.get("runTime") != null ? Long.parseLong(result.get("runTime").toString()) : null;

                Map<String, String> filesOut = (Map<String, String>) result.get("files");

                String stdout = filesOut.getOrDefault("stdout", "").trim();
                String stderr = filesOut.getOrDefault("stderr", "").trim();

                return new RunResult(stdout, stderr, status, exitStatus, time, runTime, memory);
            } else {
                return new RunResult("", "评测机返回异常状态：" + response.getStatusCode(), "Error", -1, 0, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RunResult("", e.getMessage(), "Exception", -1, 0, 0, 0);
        }
    }


}
