package com.hashclick.taskservice.dto;

// Mirrors user-service UserResponse — used when task-service calls user-service
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;

    public Long getId()     { return id; }
    public String getName() { return name; }
    public String getEmail(){ return email; }
    public String getRole() { return role; }

    public void setId(Long id)          { this.id = id; }
    public void setName(String name)    { this.name = name; }
    public void setEmail(String email)  { this.email = email; }
    public void setRole(String role)    { this.role = role; }
}
