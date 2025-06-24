package com.ex.boot.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Data
public class RunResult {
    private Integer id;            // 主键，自增
    private Integer noteId;        // 外键，关联的笔记ID

    private String stdout;
    private String stderr;
    private String status;
    private int exitStatus;
    private long time;
    private long runTime;
    private long memory;

    private String curTitle;       // 运行时的代码标题快照
    private String curCode;        // 运行时的代码快照
    private String curInput;       // 运行时的输入内容
    private String curType;        // 运行时的语言类型，如 C++

    private Date createTime;  // 创建时间（运行时间）

    public RunResult() {}
    public RunResult(String stdout, String stderr, String status, int exitStatus, long time, long runTime, long memory) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.status = status;
        this.exitStatus = exitStatus;
        this.time = time;
        this.runTime = runTime;
        this.memory = memory;
    }

    @Override
    public String toString() {
        return String.format("状态：%s\n退出码：%d\n执行时间：%d ns\n内存：%d bytes\n运行时长：%d ns\n\n输出：\n%s\n\n错误：\n%s",
                status, exitStatus, time, memory, runTime,
                stdout.isEmpty() ? "[无输出]" : stdout,
                stderr.isEmpty() ? "[无错误]" : stderr);
    }

}
