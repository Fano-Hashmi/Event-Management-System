package model;

/**
 * Represents a user in the Event Management System.
 * Supports roles: ADMIN, STAFF, USER.
 * Supports status: ACTIVE, BANNED.
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String role;   // ADMIN, STAFF, USER
    private String status; // ACTIVE, BANNED

    public User() {}

    public User(int userId, String name, String email, String password, String role, String status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
    public boolean isStaff() { return "STAFF".equalsIgnoreCase(role); }
    public boolean isUser() { return "USER".equalsIgnoreCase(role); }
    public boolean isBanned() { return "BANNED".equalsIgnoreCase(status); }
    public boolean isActive() { return "ACTIVE".equalsIgnoreCase(status); }

    @Override
    public String toString() {
        return "User{id=" + userId + ", name='" + name + "', email='" + email +
               "', role='" + role + "', status='" + status + "'}";
    }
}
