package com.example.kajimate.service;

import java.time.LocalDate;
import java.util.List;

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

    // タスクを全て取得する
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    // 今日のタスクを全て取得する（未完了のみ）
    public List<Task> findAllTasksWithEndDateToday() {
        return taskRepository.findAllTasksWithEndDateToday();
    }

    // 期限切れのタスクを全て取得する（未完了のみ）
    public List<Task> findAllTasksWithEndDateoverdueToday() {
        return taskRepository.findAllTasksWithEndDateoverdueToday();
    }

    // lineUserIDからユーザーに紐づく期限切れのタスクを取得する（未完了のみ）
    public List<Task> findTodayTasksByLineUserId(String lineUserId) {
        return taskRepository.findTodayTasksByLineUserId(lineUserId, LocalDate.now());
    }

    // lineUserIDからユーザーに紐づく期限切れのタスクを取得する（未完了のみ）
    public List<Task> findOverdueTasksByLineUserId(String lineUserId) {
        return taskRepository.findOverdueTasksByLineUserId(lineUserId, LocalDate.now());
    }

    // 締切日が明日のタスクを全て取得する（未完了のみ）→リマインド通知用
    public List<Task> findByEndDateAndStatus(LocalDate targetDate) {
        return taskRepository.findByEndDateAndStatus(targetDate);
    }

    // lineUserIdとタスクタイトルからタスクを1件取得する。
    public Task findTaskOrThrow(String lineUserId, String title) {
        return taskRepository.findFirstByUserIdAndTitleAndEndDate(lineUserId, title)
                .orElseThrow(() -> new TaskNotFoundException("タスクが見つかりません。：" + title));
    }

    // lineUserIdとタスクタイトルから取得したタスク1件のステータスを更新する
    public void completeTask(String lineUserId, String title) {
        Task task = findTaskOrThrow(lineUserId, title);
        task.setStatus("完了");
        taskRepository.save(task);
    }

    // lineUserIdとタスクタイトルから取得したタスク1件を削除する。
    public void deleteTask(String lineUserId, String title) {
        Task task = findTaskOrThrow(lineUserId, title);
        taskRepository.delete(task);
    }

}
