package com.hashclick.taskservice.controller;

import com.hashclick.taskservice.dto.TaskRequest;
import com.hashclick.taskservice.dto.TaskResponse;
import com.hashclick.taskservice.enums.TaskStatus;
import com.hashclick.taskservice.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request,
                                                   @AuthenticationPrincipal String email,
                                                   HttpServletRequest httpRequest) {
        return ResponseEntity.status(201).body(
                taskService.createTask(request, email, token(httpRequest)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all tasks (admin sees all, user sees own)")
    public ResponseEntity<List<TaskResponse>> getAllTasks(@AuthenticationPrincipal String email,
                                                          HttpServletRequest httpRequest) {
        return ResponseEntity.ok(taskService.getAllTasksForUser(email, token(httpRequest)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id,
                                                @AuthenticationPrincipal String email,
                                                HttpServletRequest httpRequest) {
        return ResponseEntity.ok(taskService.getTaskById(id, email, token(httpRequest)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get tasks assigned to me")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal String email,
                                                          HttpServletRequest httpRequest) {
        return ResponseEntity.ok(taskService.getMyTasks(email, token(httpRequest)));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get overdue tasks")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(@AuthenticationPrincipal String email,
                                                               HttpServletRequest httpRequest) {
        return ResponseEntity.ok(taskService.getOverdueTasks(email, token(httpRequest)));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Filter tasks by status")
    public ResponseEntity<List<TaskResponse>> getByStatus(@PathVariable TaskStatus status,
                                                           @AuthenticationPrincipal String email,
                                                           HttpServletRequest httpRequest) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status, email, token(httpRequest)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Update a task (creator or admin only)")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest request,
                                                   @AuthenticationPrincipal String email,
                                                   HttpServletRequest httpRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, request, email, token(httpRequest)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Update task status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id,
                                                     @RequestParam TaskStatus status,
                                                     @AuthenticationPrincipal String email,
                                                     HttpServletRequest httpRequest) {
        return ResponseEntity.ok(taskService.updateStatus(id, status, email, token(httpRequest)));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Assign task to a user (admin only)")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Long id,
                                                   @RequestParam Long userId,
                                                   HttpServletRequest httpRequest) {
        return ResponseEntity.ok(taskService.assignTask(id, userId, token(httpRequest)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Delete a task (creator or admin only)")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
                                           @AuthenticationPrincipal String email,
                                           HttpServletRequest httpRequest) {
        taskService.deleteTask(id, email, token(httpRequest));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Task dashboard statistics (admin only)")
    public ResponseEntity<Map<String, Long>> stats() {
        long[] s = taskService.getDashboardStats();
        return ResponseEntity.ok(Map.of(
                "totalTasks", s[0], "done", s[1],
                "inProgress", s[2], "overdue", s[3]));
    }

    private String token(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
