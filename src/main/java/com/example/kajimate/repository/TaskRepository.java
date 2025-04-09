package com.example.kajimate.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.kajimate.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAll();

    @Query("SELECT t FROM Task t WHERE t.endDate = CURRENT_DATE AND t.status = '未完了'")
    List<Task> findAllTasksWithEndDateToday();

    @Query("SELECT t FROM Task t WHERE t.endDate < CURRENT_DATE AND t.status = '未完了'")
    List<Task> findAllTasksWithEndDateoverdueToday();

    @Query("SELECT t FROM Task t WHERE t.user.lineUserId = :lineUserId AND t.endDate = :today AND t.status = '未完了'")
    List<Task> findTodayTasksByLineUserId(@Param("lineUserId") String lineUserId, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.user.lineUserId = :lineUserId AND t.endDate < :today AND t.status = '未完了'")
    List<Task> findOverdueTasksByLineUserId(@Param("lineUserId") String lineUserId, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.user.lineUserId = :lineUserId AND t.title = :title AND t.status = '未完了' ORDER BY t.endDate ASC LIMIT 1")
    Optional<Task> findFirstByUserIdAndTitleAndEndDate(
            @Param("lineUserId") String lineUserId,
            @Param("title") String title);

    @Query("SELECT t FROM Task t JOIN FETCH t.user WHERE t.endDate = :targetDate AND t.status = '未完了'")
    List<Task> findByEndDateAndStatus(@Param("targetDate") LocalDate targetDate);

}
