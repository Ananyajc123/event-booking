package com.eventbooking.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false) private String email;
    @Column(nullable = false) private String password;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String role = "USER";
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private List<Booking> bookings;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public User() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; } public void setEmail(String v) { this.email = v; }
    public String getPassword() { return password; } public void setPassword(String v) { this.password = v; }
    public String getName() { return name; } public void setName(String v) { this.name = v; }
    public String getRole() { return role; } public void setRole(String v) { this.role = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Booking> getBookings() { return bookings; }
    public static UserBuilder builder() { return new UserBuilder(); }
    public static class UserBuilder {
        private String email, password, name, role = "USER";
        public UserBuilder email(String v) { this.email = v; return this; }
        public UserBuilder password(String v) { this.password = v; return this; }
        public UserBuilder name(String v) { this.name = v; return this; }
        public UserBuilder role(String v) { this.role = v; return this; }
        public User build() { User u = new User(); u.email=email; u.password=password; u.name=name; u.role=role; return u; }
    }
}
