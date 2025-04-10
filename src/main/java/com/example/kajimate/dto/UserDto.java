package com.example.kajimate.dto;

public class UserDto {
    private Long userId;
    private String userName;

    public UserDto(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    // Getter & Setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}