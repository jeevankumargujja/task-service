package com.hashclick.taskservice.model;

import com.hashclick.taskservice.enums.Priority;
import com.hashclick.taskservice.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String title;

    @Size(max = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    private LocalDate dueDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // Store user IDs — users live in user-service
    @Column(nullable = false)
    private Long createdById;

    private Long assignedToId;

    public Task() {}

    @PreUpdate
    public void onUpdate() { this.updatedAt = LocalDateTime.now(); }

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

    public void setId(Long id)                  { this.id = id; }
    public void setTitle(String title)          { this.title = title; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setStatus(TaskStatus status)    { this.status = status; }
    public void setPriority(Priority priority)  { this.priority = priority; }
    public void setDueDate(LocalDate d)         { this.dueDate = d; }
    public void setCreatedById(Long id)         { this.createdById = id; }
    public void setAssignedToId(Long id)        { this.assignedToId = id; }
    public void setUpdatedAt(LocalDateTime dt)  { this.updatedAt = dt; }
}
