package com.ex.boot.Test;

import com.ex.boot.model.Note;
import com.ex.boot.model.RunResult;
import com.ex.boot.service.RunCodeService;

public class RunCodeTest {
    public static void main(String[] args) {
        RunCodeService service = new RunCodeService();

        Note note = new Note();
        note.setType("Python");
        note.setContent("a, b = map(int, input().split())\nprint(a + b)");
        note.setInput("3 4");

        RunResult output = service.runCode(note);
        System.out.println("运行输出：\n" + output);
    }
}
