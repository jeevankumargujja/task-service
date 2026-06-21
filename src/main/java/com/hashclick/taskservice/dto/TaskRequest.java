package com.hashclick.taskservice.dto;

import com.hashclick.taskservice.enums.Priority;
import com.hashclick.taskservice.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TaskRequest {

    @NotBlank @Size(min = 3, max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    private TaskStatus status;

    private Priority priority = Priority.MEDIUM;

    @FutureOrPresent
    private LocalDate dueDate;

    private Long assignedToUserId;

    public String getTitle()             { return title; }
    public String getDescription()       { return description; }
    public TaskStatus getStatus()        { return status; }
    public Priority getPriority()        { return priority; }
    public LocalDate getDueDate()        { return dueDate; }
    public Long getAssignedToUserId()    { return assignedToUserId; }

    public void setTitle(String title)              { this.title = title; }
    public void setDescription(String description)  { this.description = description; }
    public void setStatus(TaskStatus status)        { this.status = status; }
    public void setPriority(Priority priority)      { this.priority = priority; }
    public void setDueDate(LocalDate dueDate)       { this.dueDate = dueDate; }
    public void setAssignedToUserId(Long id)        { this.assignedToUserId = id; }
}
