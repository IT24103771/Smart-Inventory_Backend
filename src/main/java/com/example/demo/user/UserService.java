package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // -------- AUTH --------

    public Map<String, Object> register(User user) {

        String fullName = user.getFullName() == null ? "" : user.getFullName().trim();
        String email = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase();
        String password = user.getPassword() == null ? "" : user.getPassword();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return msg(false, "Please fill all fields");
        }

        if (userRepository.existsByEmail(email)) {
            return msg(false, "Email already registered");
        }

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setRole(user.getRole() == null ? "OWNER" : user.getRole().trim().toUpperCase());
        user.setStatus("ACTIVE");

        User saved = userRepository.save(user);
        return userInfo(true, "Registration successful", saved);
    }

    public Map<String, Object> login(String email, String password) {

        String cleanEmail = email == null ? "" : email.trim().toLowerCase();
        String cleanPassword = password == null ? "" : password;

        return userRepository.findByEmail(cleanEmail)
                .map(user -> {
                    if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                        return msg(false, "Account is deactivated");
                    }

                    if (!encoder.matches(cleanPassword, user.getPassword())) {
                        return msg(false, "Invalid email or password");
                    }

                    return userInfo(true, "Login successful", user);
                })
                .orElseGet(() -> msg(false, "Invalid email or password"));
    }

    // -------- ADMIN CRUD --------

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return new UserResponse(u);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        if (req.getFullName() != null && !req.getFullName().trim().isEmpty()) {
            u.setFullName(req.getFullName().trim());
        }

        if (req.getEmail() != null && !req.getEmail().trim().isEmpty()) {
            String newEmail = req.getEmail().trim().toLowerCase();
            if (!newEmail.equals(u.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email already in use");
            }
            u.setEmail(newEmail);
        }

        if (req.getRole() != null && !req.getRole().trim().isEmpty()) {
            u.setRole(req.getRole().trim().toUpperCase());
        }

        if (req.getStatus() != null && !req.getStatus().trim().isEmpty()) {
            u.setStatus(req.getStatus().trim().toUpperCase());
        }

        if (req.getNewPassword() != null && !req.getNewPassword().isEmpty()) {
            u.setPassword(encoder.encode(req.getNewPassword()));
        }

        User saved = userRepository.save(u);
        return new UserResponse(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserResponse setUserStatus(Long id, String status) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        u.setStatus(status == null ? "ACTIVE" : status.trim().toUpperCase());
        return new UserResponse(userRepository.save(u));
    }

    // -------- helpers --------

    private Map<String, Object> msg(boolean ok, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", ok);
        m.put("message", message);
        return m;
    }

    private Map<String, Object> userInfo(boolean ok, String message, User user) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", ok);
        res.put("message", message);

        Map<String, Object> u = new HashMap<>();
        u.put("id", user.getId());
        u.put("fullName", user.getFullName());
        u.put("email", user.getEmail());
        u.put("role", user.getRole());
        u.put("status", user.getStatus());

        res.put("user", u); // ✅ fixed key
        return res;
    }
}
