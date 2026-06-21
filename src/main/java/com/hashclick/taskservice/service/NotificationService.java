package com.hashclick.taskservice.service;

import com.hashclick.taskservice.dto.UserResponse;
import com.hashclick.taskservice.model.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Value("${notifications.email.enabled:false}")
    private boolean emailEnabled;

    @Async
    public void notifyTaskAssigned(UserResponse assignee, Task task) {
        if (emailEnabled) {
            // Wire real JavaMailSender here when email is enabled
        }
        System.out.printf("[NOTIFY] Task '%s' assigned to %s (%s)%n",
                task.getTitle(), assignee.getName(), assignee.getEmail());
    }

    @Async
    public void notifyOverdue(UserResponse user, Task task) {
        System.out.printf("[OVERDUE] Task '%s' is overdue for %s (%s)%n",
                task.getTitle(), user.getName(), user.getEmail());
    }
}
