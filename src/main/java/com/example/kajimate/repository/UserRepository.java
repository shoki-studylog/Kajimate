package com.example.kajimate.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kajimate.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // ユーザーネームからユーザーを特定する。
    Optional<User> findByUsername(String username);

}