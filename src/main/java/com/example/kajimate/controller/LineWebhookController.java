package com.example.kajimate.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kajimate.entity.Task;
import com.example.kajimate.repository.TaskRepository;
import com.example.kajimate.service.LineMessagingService;
import com.example.kajimate.service.TaskService;

@RestController
@RequestMapping("/webhook")
public class LineWebhookController {

    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final LineMessagingService lineMessagingService;

    public LineWebhookController(TaskRepository taskRepository, TaskService taskService,
            LineMessagingService lineMessagingService) {
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.lineMessagingService = lineMessagingService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        List<Task> tasks = null;
        Task task = null;
        List<Map<String, Object>> events = (List<Map<String, Object>>) payload.get("events");

        for (Map<String, Object> event : events) {
            Map<String, Object> message = (Map<String, Object>) event.get("message");
            String text = (String) message.get("text");
            System.out.println("受信内容：" + text);

            Map<String, Object> source = (Map<String, Object>) event.get("source");
            String lineUserId = (String) source.get("userId");
            String replyToken = (String) event.get("replyToken");

            try {
                switch (text) {
                    case "今日のタスク":
                        tasks = taskRepository.findTodayTasksByLineUserId(lineUserId, LocalDate.now());
                        System.out.println("今日のタスクを受信しました。");
                        break;

                    case "期限切れのタスク":
                        tasks = taskRepository.findOverdueTasksByLineUserId(lineUserId, LocalDate.now());
                        System.out.println("期限切れのタスクを受信しました。");
                        break;
                    case "新規タスク":
                        tasks = List.of();
                        break;
                    default:
                        if (text.startsWith("完了")) {
                            taskService.completeTask(lineUserId, text.substring(3));
                            System.out.println("完了を受信しました。");
                            text = "「" + text.substring(3) + "」" + "を更新しました";
                            tasks = List.of();
                            System.out.println("完了処理対象: " + text);
                        } else if (text.startsWith("削除")) {
                            System.out.println("削除を受信しました。");
                            taskService.deleteTask(lineUserId, text.substring(3));
                            text = "「" + text.substring(3) + "」" + "を削除しました";
                            tasks = List.of();
                            System.out.println("完了処理対象: " + text);
                        } else if (text.matches("^.+、\\d{8}、.+$")) {
                            System.out.println("新規タスク内容を受信しました。");
                            tasks = List.of();
                        } else {
                            task = taskService.findTaskOrThrow(lineUserId, text);
                            System.out.println("タスク名を受信しました。");
                            // 単一タスクを1件のリストにして表示
                            tasks = List.of(task);
                            System.out.println("メニュー表示パターン: " + text);
                        }
                        break;
                }

                // 正常時：返信
                lineMessagingService.replyTasks(replyToken, tasks, text);

            } catch (Exception e) {
                // 例外が発生した場合：ユーザーにメッセージを返す
                String errorMessage = "タスク「" + text + "」は見つかりませんでした。もう一度確認してください。";
                lineMessagingService.replyText(replyToken, errorMessage);
                e.printStackTrace(); // ログにも出しておくと便利
            }

        }

        return ResponseEntity.ok("OK");
    }
}
