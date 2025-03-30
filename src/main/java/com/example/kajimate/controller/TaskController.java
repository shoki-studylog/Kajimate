package com.example.kajimate.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.kajimate.entity.Task;
import com.example.kajimate.repository.TaskRepository;
import com.example.kajimate.repository.UserRepository;

@Controller
public class TaskController {

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String requestMethodName(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        model.addAttribute("todaysTasks", taskRepository.findAllTasksWithEndDateToday());
        model.addAttribute("OverdueTasks", taskRepository.findAllTasksWithEndDateoverdueToday());
        return "home";
    }

    @GetMapping("/task/new")
    public String getMethodName(Model model) {
        model.addAttribute("newTask", new Task());
        model.addAttribute("users", userRepository.findAll());
        return "create_task";
    }

    @PostMapping("/home")
    public String createTask(@ModelAttribute Task task) {
        taskRepository.save(task);
        return "redirect:/";
    }

    @GetMapping("/tasks/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        model.addAttribute("users", userRepository.findAll());
        Optional<Task> taskOpt = taskRepository.findById(id);

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            model.addAttribute("task", task);
            return "edit_task"; // Thymeleaf のテンプレート名
        } else {
            return "redirect:/home"; // エラーハンドリング
        }
    }

    @PostMapping("/tasks/update")
    public String updateTask(@ModelAttribute("task") Task updatedTask) {
        System.out.println("受け取ったendDate: " + updatedTask.getEndDate());
        taskRepository.save(updatedTask);
        return "redirect:/";
    }

    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定されたタスクIDが存在しません: " + id));
        taskRepository.delete(task);
        return "redirect:/";
    }

}
