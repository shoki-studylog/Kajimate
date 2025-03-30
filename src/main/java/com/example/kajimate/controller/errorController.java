package com.example.kajimate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class errorController {
    @GetMapping("/error_403")
    public String accessDenied() {
        return "error_403"; // templates/error_403.html を表示
    }

}
