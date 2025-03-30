package com.example.kajimate.dto;

import java.util.ArrayList;
import java.util.List;

public class TaskUserDto {
    private Long taskId;
    private String taskName;
    private List<UserDto> users;

    public TaskUserDto(Long taskId, String taskName, List<UserDto> users) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.users = users != null ? users : new ArrayList<>();
    }

    // Getter & Setter
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<UserDto> getUsers() {
        return users != null ? users : new ArrayList<>();
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
}
