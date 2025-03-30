package com.example.kajimate.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.kajimate.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAll();

    @Query("SELECT t FROM Task t WHERE t.endDate = CURRENT_DATE")
    List<Task> findAllTasksWithEndDateToday();

    @Query("SELECT t FROM Task t WHERE t.endDate < CURRENT_DATE")
    List<Task> findAllTasksWithEndDateoverdueToday();

}
