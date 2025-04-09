package com.example.kajimate.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.kajimate.controller.AdminController;
import com.example.kajimate.controller.AuthController;
import com.example.kajimate.entity.Task;
import com.example.kajimate.entity.User;
import com.example.kajimate.repository.TaskRepository;
import com.example.kajimate.repository.UserRepository;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.TextMessage;

@Service
public class LineMessagingService {

    private final TaskRepository taskRepository;

    private final AuthController authController;
    private final AdminController adminController;
    private final UserRepository userRepository;

    @Value("${line.bot.channel-token}")
    private String CHANNEL_ACCESS_TOKEN;

    private final RestTemplate restTemplate = new RestTemplate();
    private final LineMessagingClient lineMessagingClient;

    public LineMessagingService(LineMessagingClient lineMessagingClient, AdminController adminController,
            AuthController authController, UserRepository userRepository, TaskRepository taskRepository) {
        this.lineMessagingClient = lineMessagingClient;
        this.adminController = adminController;
        this.authController = authController;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public void replyTasks(String replyToken, List<Task> tasks, String message) {
        String text;

        switch (message) {
            case "今日のタスク":
                if (tasks.isEmpty()) {
                    text = "本日のタスクはありません 😊";
                } else {
                    text = "📅 今日のタスク一覧\n" +
                            tasks.stream()
                                    .map(t -> "・" + t.getTitle() + "\n" + "締切：" + t.getEndDate())
                                    .collect(Collectors.joining("\n"));
                }
                break;
            case "期限切れのタスク":
                if (tasks.isEmpty()) {
                    text = "期限切れのタスクはありません 😊";
                } else {
                    text = "📅 期限切れのタスク一覧\n" +
                            tasks.stream()
                                    .map(t -> "・" + t.getTitle() + "\n" + "締切：" + t.getEndDate())
                                    .collect(Collectors.joining("\n"));
                }
                break;
            case "新規タスク":
                text = "タスク名、締切日、担当者を入力してください" + "\n" + "例)洗濯、2020501、shoki";
                break;
            default:
                if (message.matches("^(.+?)\\s*、\\s*(\\d{8})\\s*、\\s*(.+)$")) {
                    text = message;
                } else if (tasks == null || tasks.isEmpty()) {
                    text = "更新・削除が完了しました。";
                } else {
                    text = "タスク名：" + tasks.get(0).getTitle();
                }
                break;
        }
        replyMessage(replyToken, text);
    }

    private void replyMessage(String replyToken, String messageText) {
        String url = "https://api.line.me/v2/bot/message/reply";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(CHANNEL_ACCESS_TOKEN);
        Map<String, Object> body;

        // タスク新規登録時に使用する正規表現
        Pattern pattern = Pattern.compile("^(.+?)\\s*、\\s*(\\d{8})\\s*、\\s*(.+)$");
        Matcher matcher = pattern.matcher(messageText);

        if (messageText.contains("タスク名：")) {
            messageText = messageText.replace("タスク名：", "");
            // ボタンテンプレートの JSON 構築
            Map<String, Object> actionUpdate = Map.of(
                    "type", "message",
                    "label", "完了",
                    "text", "完了 " + messageText);
            Map<String, Object> actionDelete = Map.of(
                    "type", "message",
                    "label", "削除",
                    "text", "削除 " + messageText);

            Map<String, Object> template = Map.of(
                    "type", "buttons",
                    "title", "タスク操作メニュー",
                    "text", "「" + messageText + "」に対して何をしますか？",
                    "actions", List.of(actionUpdate, actionDelete));

            Map<String, Object> message = Map.of(
                    "type", "template",
                    "altText", "タスク操作メニュー",
                    "template", template);

            body = Map.of(
                    "replyToken", replyToken,
                    "messages", List.of(message));
        } else if (matcher.matches()) {
            String title = matcher.group(1).trim(); // → "宿題提出"
            String Date = matcher.group(2).trim(); // → "20250410"
            String assignee = matcher.group(3).trim(); // → "山田"

            // 入力したusernameからUserを特定
            Optional<User> assignmentUser = userRepository.findByUsername(assignee);
            // 入力した締切日をLocalDateに変換
            LocalDate endDate = LocalDate.parse(Date, DateTimeFormatter.ofPattern("yyyyMMdd"));

            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setEndDate(endDate);
            newTask.setUser(assignmentUser.get());
            taskRepository.save(newTask);

            Map<String, Object> message = Map.of("type", "text", "text",
                    "新規タスクを登録しました。" + "\n" +
                            "登録内容は下記です。" + "\n" +
                            "タスク名：" + title + "\n" +
                            "締切日：" + Date + "\n" +
                            "担当者：" + assignee);
            // Line APIの仕様に合わせて、replyTokenとmessagesを含むMapを作成
            body = Map.of("replyToken", replyToken, "messages", List.of(message));

        } else {
            Map<String, Object> message = Map.of("type", "text", "text", messageText);
            // Line APIの仕様に合わせて、replyTokenとmessagesを含むMapを作成
            body = Map.of("replyToken", replyToken, "messages", List.of(message));
        }
        // HttpEntityを作成
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        // POSTリクエストを送信
        restTemplate.postForObject(url, request, String.class);
    }

    public void replyText(String replyToken, String message) {
        TextMessage textMessage = new TextMessage(message);
        ReplyMessage replyMessage = new ReplyMessage(replyToken, List.of(textMessage));
        lineMessagingClient.replyMessage(replyMessage);
    }

    // @Scheduled(cron = "0 0 7 * * *") // 日本時間7時（= UTC22時）
    @Scheduled(fixedRate = 60000) // 毎分実行（1分 = 60,000ミリ秒） ※テスト用
    public void remindTasks() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Task> tasks = taskRepository.findByEndDateAndStatus(tomorrow);

        for (Task task : tasks) {
            String lineUserId = task.getUser().getLineUserId();
            if (lineUserId == null || lineUserId.isBlank())
                continue;

            String message = "🔔【リマインド通知】\n"
                    + "明日が締切のタスクがあります！\n\n"
                    + "タスク名：" + task.getTitle() + "\n"
                    + "締切日：" + task.getEndDate();

            TextMessage textMessage = new TextMessage(message);
            PushMessage pushMessage = new PushMessage(lineUserId, textMessage);

            lineMessagingClient.pushMessage(pushMessage);

            // リマインド時間を保存（task に `remindTime` フィールドがある前提）
            task.setRemindTime(LocalDateTime.now());
            taskRepository.save(task);
        }
    }

}