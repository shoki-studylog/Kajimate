package com.example.kajimate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.kajimate.repository.TaskRepository;
import com.example.kajimate.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    AdminController(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("tasks", taskRepository.findAll());
        return "admin_home"; // admin_home.html を表示
    }
}
