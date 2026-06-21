package com.hashclick.taskservice.service;

import com.hashclick.taskservice.model.Task;
import com.hashclick.taskservice.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class OverdueAlertScheduler {

    private final TaskRepository taskRepository;

    public OverdueAlertScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Runs daily at 8 AM — logs overdue tasks (notify via user-service lookup if needed)
    @Scheduled(cron = "0 0 8 * * *")
    public void alertOverdueTasks() {
        List<Task> overdue = taskRepository.findOverdueTasks(LocalDate.now());
        if (overdue.isEmpty()) return;
        System.out.println("[SCHEDULER] " + overdue.size() + " overdue task(s) found:");
        overdue.forEach(t -> System.out.printf("  - [%d] %s (due: %s, assignedTo: %s)%n",
                t.getId(), t.getTitle(), t.getDueDate(), t.getAssignedToId()));
    }
}
