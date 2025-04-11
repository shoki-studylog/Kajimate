package com.example.kajimate.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.kajimate.entity.User;
import com.example.kajimate.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
