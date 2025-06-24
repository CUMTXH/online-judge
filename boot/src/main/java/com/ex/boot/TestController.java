package com.ex.boot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
    @RequestMapping("/test1")
    public String test1(Model model) {
        model.addAttribute("msg", "Hello World");
        return "login";
    }


}
