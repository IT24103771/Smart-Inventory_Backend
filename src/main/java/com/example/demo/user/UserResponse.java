package com.example.demo.user;

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String status;

    public UserResponse() {}

    public UserResponse(User u) {
        this.id = u.getId();
        this.fullName = u.getFullName();
        this.email = u.getEmail();
        this.role = u.getRole();
        this.status = u.getStatus();
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}
