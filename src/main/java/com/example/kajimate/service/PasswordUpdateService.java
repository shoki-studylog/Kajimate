package com.example.kajimate.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.kajimate.entity.User;
import com.example.kajimate.repository.UserRepository;

@Service
public class PasswordUpdateService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordUpdateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updatePasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getPassword().startsWith("$2a$")) { // すでにハッシュ化済みかチェック
                user.setPassword(passwordEncoder.encode(user.getPassword())); // ハッシュ化
                userRepository.save(user);
            }
        }
    }
}