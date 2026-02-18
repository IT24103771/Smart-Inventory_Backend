package com.example.demo.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
        user.setPassword(encoder.encode(password)); // hash
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

        res.put("com/example/demo/user", u);
        return res;
    }
}
