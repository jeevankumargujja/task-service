package com.hashclick.taskservice.dto;

import com.hashclick.taskservice.enums.Priority;
import com.hashclick.taskservice.enums.TaskStatus;
import com.hashclick.taskservice.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private Long assignedToId;
    private boolean overdue;

    public static TaskResponse from(Task task) {
        TaskResponse r = new TaskResponse();
        r.id = task.getId();
        r.title = task.getTitle();
        r.description = task.getDescription();
        r.status = task.getStatus();
        r.priority = task.getPriority();
        r.dueDate = task.getDueDate();
        r.createdAt = task.getCreatedAt();
        r.updatedAt = task.getUpdatedAt();
        r.createdById = task.getCreatedById();
        r.assignedToId = task.getAssignedToId();
        r.overdue = task.getDueDate() != null
                && task.getDueDate().isBefore(LocalDate.now())
                && task.getStatus() != TaskStatus.DONE;
        return r;
    }

    public Long getId()                  { return id; }
    public String getTitle()             { return title; }
    public String getDescription()       { return description; }
    public TaskStatus getStatus()        { return status; }
    public Priority getPriority()        { return priority; }
    public LocalDate getDueDate()        { return dueDate; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getUpdatedAt()  { return updatedAt; }
    public Long getCreatedById()         { return createdById; }
    public Long getAssignedToId()        { return assignedToId; }
    public boolean isOverdue()           { return overdue; }
}
