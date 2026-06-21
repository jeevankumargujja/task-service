package com.hashclick.taskservice.repository;

import com.hashclick.taskservice.enums.TaskStatus;
import com.hashclick.taskservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedToIdOrCreatedById(Long assignedToId, Long createdById);

    List<Task> findByAssignedToId(Long assignedToId);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByStatusAndAssignedToIdOrStatusAndCreatedById(
            TaskStatus s1, Long assignedToId, TaskStatus s2, Long createdById);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status != 'DONE' " +
           "AND (t.assignedToId = :userId OR t.createdById = :userId)")
    List<Task> findOverdueTasksForUser(@Param("today") LocalDate today, @Param("userId") Long userId);

    long countByStatus(TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :today AND t.status != 'DONE'")
    long countOverdue(@Param("today") LocalDate today);
}
