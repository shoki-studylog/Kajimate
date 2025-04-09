package com.example.kajimate.service;

import org.springframework.stereotype.Service;

import com.example.kajimate.entity.Task;
import com.example.kajimate.exception.TaskNotFoundException;
import com.example.kajimate.repository.TaskRepository;

@Service
public class TaskService {
    TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private final TaskRepository taskRepository;

    public Task findTaskOrThrow(String lineUserId, String title) {
        return taskRepository.findFirstByUserIdAndTitleAndEndDate(lineUserId, title)
                .orElseThrow(() -> new TaskNotFoundException("タスクが見つかりません。：" + title));
    }

    public void completeTask(String lineUserId, String title) {
        Task task = findTaskOrThrow(lineUserId, title);
        task.setStatus("完了");
        taskRepository.save(task);
    }

    public void deleteTask(String lineUserId, String title) {
        Task task = findTaskOrThrow(lineUserId, title);
        taskRepository.delete(task);
    }

}
