package com.hashclick.taskservice.service;

import com.hashclick.taskservice.client.UserServiceClient;
import com.hashclick.taskservice.dto.TaskRequest;
import com.hashclick.taskservice.dto.TaskResponse;
import com.hashclick.taskservice.dto.UserResponse;
import com.hashclick.taskservice.enums.TaskStatus;
import com.hashclick.taskservice.exception.ResourceNotFoundException;
import com.hashclick.taskservice.model.Task;
import com.hashclick.taskservice.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository,
                       UserServiceClient userServiceClient,
                       NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.userServiceClient = userServiceClient;
        this.notificationService = notificationService;
    }

    public TaskResponse createTask(TaskRequest request, String creatorEmail, String token) {
        UserResponse creator = userServiceClient.getUserByEmail(creatorEmail, token);
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setCreatedById(creator.getId());
        if (request.getAssignedToUserId() != null) {
            UserResponse assignee = userServiceClient.getUserById(request.getAssignedToUserId(), token);
            task.setAssignedToId(assignee.getId());
            notificationService.notifyTaskAssigned(assignee, task);
        }
        return TaskResponse.from(taskRepository.save(task));
    }

    public List<TaskResponse> getAllTasksForUser(String email, String token) {
        UserResponse user = userServiceClient.getUserByEmail(email, token);
        if (isAdmin(user))
            return taskRepository.findAll().stream().map(TaskResponse::from).toList();
        return taskRepository.findByAssignedToIdOrCreatedById(user.getId(), user.getId())
                .stream().map(TaskResponse::from).toList();
    }

    public TaskResponse getTaskById(Long id, String email, String token) {
        Task task = findTask(id);
        UserResponse user = userServiceClient.getUserByEmail(email, token);
        if (!isAdmin(user) && !isParticipant(task, user.getId()))
            throw new AccessDeniedException("You don't have access to this task");
        return TaskResponse.from(task);
    }

    public List<TaskResponse> getMyTasks(String email, String token) {
        UserResponse user = userServiceClient.getUserByEmail(email, token);
        return taskRepository.findByAssignedToId(user.getId())
                .stream().map(TaskResponse::from).toList();
    }

    public List<TaskResponse> getOverdueTasks(String email, String token) {
        UserResponse user = userServiceClient.getUserByEmail(email, token);
        if (isAdmin(user))
            return taskRepository.findOverdueTasks(LocalDate.now()).stream().map(TaskResponse::from).toList();
        return taskRepository.findOverdueTasksForUser(LocalDate.now(), user.getId())
                .stream().map(TaskResponse::from).toList();
    }

    public List<TaskResponse> getTasksByStatus(TaskStatus status, String email, String token) {
        UserResponse user = userServiceClient.getUserByEmail(email, token);
        if (isAdmin(user))
            return taskRepository.findByStatus(status).stream().map(TaskResponse::from).toList();
        return taskRepository.findByStatusAndAssignedToIdOrStatusAndCreatedById(
                        status, user.getId(), status, user.getId())
                .stream().map(TaskResponse::from).toList();
    }

    public TaskResponse updateTask(Long id, TaskRequest request, String editorEmail, String token) {
        Task task = findTask(id);
        UserResponse editor = userServiceClient.getUserByEmail(editorEmail, token);
        requireCreatorOrAdmin(task, editor);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : task.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        if (request.getAssignedToUserId() != null) {
            UserResponse newAssignee = userServiceClient.getUserById(request.getAssignedToUserId(), token);
            boolean reassigned = task.getAssignedToId() == null ||
                                 !task.getAssignedToId().equals(newAssignee.getId());
            task.setAssignedToId(newAssignee.getId());
            if (reassigned) notificationService.notifyTaskAssigned(newAssignee, task);
        }
        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse updateStatus(Long id, TaskStatus status, String userEmail, String token) {
        Task task = findTask(id);
        UserResponse user = userServiceClient.getUserByEmail(userEmail, token);
        if (!isAdmin(user) && !isParticipant(task, user.getId()))
            throw new AccessDeniedException("Not authorized to update this task's status");
        task.setStatus(status);
        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse assignTask(Long taskId, Long userId, String token) {
        Task task = findTask(taskId);
        UserResponse assignee = userServiceClient.getUserById(userId, token);
        task.setAssignedToId(assignee.getId());
        Task saved = taskRepository.save(task);
        notificationService.notifyTaskAssigned(assignee, saved);
        return TaskResponse.from(saved);
    }

    public void deleteTask(Long id, String userEmail, String token) {
        Task task = findTask(id);
        UserResponse user = userServiceClient.getUserByEmail(userEmail, token);
        requireCreatorOrAdmin(task, user);
        taskRepository.delete(task);
    }

    public long[] getDashboardStats() {
        long total = taskRepository.count();
        long done = taskRepository.countByStatus(TaskStatus.DONE);
        long inProgress = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long overdue = taskRepository.countOverdue(LocalDate.now());
        return new long[]{total, done, inProgress, overdue};
    }

    private void requireCreatorOrAdmin(Task task, UserResponse user) {
        if (!isAdmin(user) && !task.getCreatedById().equals(user.getId()))
            throw new AccessDeniedException("You can only modify tasks you created");
    }

    private boolean isAdmin(UserResponse user) {
        return "ROLE_ADMIN".equals(user.getRole());
    }

    private boolean isParticipant(Task task, Long userId) {
        return task.getCreatedById().equals(userId) ||
               (task.getAssignedToId() != null && task.getAssignedToId().equals(userId));
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }
}
