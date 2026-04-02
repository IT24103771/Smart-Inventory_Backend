package com.example.demo.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("owner").isEmpty()) {
            User owner = new User();
            owner.setUsername("owner");
            owner.setPassword(passwordEncoder.encode("owner123"));
            owner.setName("System Owner");
            owner.setDoj(LocalDate.now());
            owner.setRole(UserRole.OWNER);
            owner.setRoleName("Owner");
            owner.setStatus("ACTIVE");
            owner.setEmail("owner@example.com");
            userRepository.save(owner);

            System.out.println("Default Owner account created: owner / owner123");
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("System Administrator");
            admin.setDoj(LocalDate.now());
            admin.setRole(UserRole.ADMIN);
            admin.setRoleName("Admin");
            admin.setStatus("ACTIVE");
            admin.setEmail("admin@example.com");
            userRepository.save(admin);

            System.out.println("Default Admin account created: admin / admin123");
        }

        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setName("Demo Staff Member");
            staff.setDoj(LocalDate.now());
            staff.setRole(UserRole.STAFF);
            staff.setRoleName("Staff");
            staff.setStatus("ACTIVE");
            staff.setEmail("staff@example.com");
            userRepository.save(staff);

            System.out.println("Default Staff account created: staff / staff123");
        }
    }
}