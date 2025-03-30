package com.example.kajimate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kajimate.service.PasswordUpdateService;

@RestController
@RequestMapping("/admin")
public class PasswordUpdateController {
    private final PasswordUpdateService passwordUpdateService;

    public PasswordUpdateController(PasswordUpdateService passwordUpdateService) {
        this.passwordUpdateService = passwordUpdateService;
    }

    @PostMapping("/update-passwords")
    public ResponseEntity<String> updatePasswords() {
        passwordUpdateService.updatePasswords();
        return ResponseEntity.ok("全ユーザーのパスワードをハッシュ化しました！");
    }
}
