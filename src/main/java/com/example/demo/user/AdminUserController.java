package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserResponse> all() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse one(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        return userService.updateUser(id, req);
    }

    @PatchMapping("/{id}/status")
    public UserResponse status(@PathVariable Long id, @RequestBody String status) {
        // body example: "DEACTIVATED" or "ACTIVE"
        return userService.setUserStatus(id, status.replace("\"",""));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
